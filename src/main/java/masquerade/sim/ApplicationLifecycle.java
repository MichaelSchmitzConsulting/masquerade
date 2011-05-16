package masquerade.sim;

import static masquerade.sim.HomeResolver.getArtifactsDir;
import static masquerade.sim.HomeResolver.getDbFileLocation;
import static masquerade.sim.HomeResolver.getRequestLogDir;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import masquerade.sim.channel.ChannelListenerRegistry;
import masquerade.sim.channel.ChannelListenerRegistryImpl;
import masquerade.sim.converter.CompoundConverter;
import masquerade.sim.db.ChannelChangeTrigger;
import masquerade.sim.db.DatabaseLifecycle;
import masquerade.sim.db.ModelNamespaceResolver;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.db.ModelRepositoryFactory;
import masquerade.sim.db.ModelRepositorySessionFactory;
import masquerade.sim.db.RequestHistoryCleanupJob;
import masquerade.sim.db.RequestHistorySessionFactory;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.Settings;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.impl.FileLoaderImpl;
import masquerade.sim.model.impl.SimulationRunnerImpl;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import com.db4o.ObjectContainer;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

/**
 * Class handling application startup/shutdown. Trigger by the servlet
 * container via {@link ServletContextListener}.
 */
public class ApplicationLifecycle implements ServletContextListener {

	private static final StatusLog log = StatusLogger.get(ApplicationLifecycle.class);
	private static final String APP_CONTEXT_ATTRIBUTE = "_masqApplicationContext";

	/**
	 * Retrieves the current {@link ApplicationContext} for a webapp as stored
	 * in the {@link ServletContext} as an attribute.
	 * 
	 * @param context {@link ServletContext}
	 * @return The {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext(ServletContext context) {
		return (ApplicationContext) context.getAttribute(APP_CONTEXT_ATTRIBUTE);
	}

	/**
	 * Retrieves the current {@link ApplicationContext} for a Vaadin application
	 * 
	 * @param app Vaadin application
	 * @return The {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext(Application app) {
		WebApplicationContext web = (WebApplicationContext) app.getContext();
		return (ApplicationContext) web.getHttpSession().getServletContext().getAttribute(APP_CONTEXT_ATTRIBUTE);
	}
	
	/**
	 * Application startup, initialize database, configuration and all channel listeners
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ObjectContainer modelDb = null;
		ObjectContainer historyDb = null;
		try {
			ServletContext servletContext = event.getServletContext();
			log.info("Starting masquerade simulator");
			
			// Determine db file locations
			File modelDbFile = getDbFileLocation(servletContext, DbType.MODEL);
			File historyDbFile = getDbFileLocation(servletContext, DbType.HISTORY);
			log.info("Main Database Location: " + modelDbFile.getAbsolutePath());
			log.info("History Database Location: " + historyDbFile.getAbsolutePath());
			
			// Create databases
			DatabaseLifecycle modelDbLifecycle = new DatabaseLifecycle();
			DatabaseLifecycle historyDbLifecycle = new DatabaseLifecycle();
			modelDb = modelDbLifecycle.start(modelDbFile);
			historyDb = historyDbLifecycle.start(historyDbFile);
			
			File requestLogDir = getRequestLogDir(servletContext);
			log.info("Request log dir: " + requestLogDir.getAbsolutePath());
			
			// Create request history factory
			RequestHistoryFactory requestHistoryFactory = new RequestHistorySessionFactory(historyDb, requestLogDir);
			
			// Create converter
			Converter converter = new CompoundConverter();
			
			// Create file loader
			File artifactsRoot = getArtifactsDir(servletContext);
			FileLoader fileLoader = new FileLoaderImpl(artifactsRoot);
			
			// Create model repository factory
			ModelRepositoryFactory modelRepositoryFactory = new ModelRepositorySessionFactory(modelDb);

			// Create namespace resolver
			NamespaceResolver namespaceResolver = new ModelNamespaceResolver(modelRepositoryFactory);
			
			// Create configuration variable holder
			ConfigurationVariableHolder configVariableHolder = new ConfigurationVariableHolder(); 
			
			// Create simulation runner
			SimulationRunner simulationRunner = new SimulationRunnerImpl(requestHistoryFactory, converter, fileLoader, namespaceResolver, configVariableHolder);
			
			// Create channel listener registry
			ChannelListenerRegistry listenerRegistry = new ChannelListenerRegistryImpl(simulationRunner);
			
			// Add channel change trigger
			registerChannelChangeTrigger(modelDb, listenerRegistry);
			
			ModelRepository repo = modelRepositoryFactory.startModelRepositorySession();
			try {
				Settings settings = repo.getSettings();
				
				// Create history cleanup job
				RequestHistoryCleanupJob cleanupJob = new RequestHistoryCleanupJob(requestHistoryFactory);
				
				// Create settings change listener, apply settings
				SettingsChangeListener settingsChangeListener = new AppSettingsChangeListener(cleanupJob, configVariableHolder);
				settingsChangeListener.settingsChanged(Settings.NO_SETTINGS, settings);
				
				// Create application context
				ApplicationContext applicationContext = new ApplicationContext(modelDbLifecycle, historyDbLifecycle, listenerRegistry, requestHistoryFactory, 
						modelRepositoryFactory, fileLoader, converter, artifactsRoot, namespaceResolver, cleanupJob, settingsChangeListener, configVariableHolder);
				
				// Save application context reference in servlet context
				servletContext.setAttribute(APP_CONTEXT_ATTRIBUTE, applicationContext);
			
				// Start channels
				listenerRegistry.startAll(repo.getChannels());
				
				// Start request history cleanup job
				cleanupJob.start();
			} finally {
				repo.endSession();
			}
						
			servletContext.log("Simulator started");
		} catch (IOException ex) {
			close(modelDb);
			close(historyDb);
			throw new IllegalArgumentException("Cannot create dir", ex);
		} catch (RuntimeException t) {
			close(modelDb);
			close(historyDb);
			throw t;
		}
	}

	/**
	 * Application is being shut down, stop
	 * all listeners and remove the application
	 * context from the servlet context.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		log.info("Shutting down masquerade simulator");
		ApplicationContext context = (ApplicationContext) servletContext.getAttribute(APP_CONTEXT_ATTRIBUTE);
		if (context != null) {
			context.getRequestHistoryCleanupJob().stop();
			stopChannels(context.getChannelListenerRegistry());
			context.getModelDb().stop();
			context.getHistoryDb().stop();
			servletContext.removeAttribute(APP_CONTEXT_ATTRIBUTE);
		}
	}

	private static void stopChannels(ChannelListenerRegistry channelListenerRegistry) {
		try {
			channelListenerRegistry.stopAll();
		} catch (Throwable t) {
			log.error("Exception while stopping all channel listeners", t);
		}
	}

	private static void registerChannelChangeTrigger(ObjectContainer db, ChannelListenerRegistry channelListenerRegistry) {
		EventRegistry events = EventRegistryFactory.forObjectContainer(db);
		events.committed().addListener(new ChannelChangeTrigger(channelListenerRegistry));
	}

	/**
	 * Closes an {@link ObjectContainer} if not <code>null</code>
	 * 
	 * @param db {@link ObjectContainer}
	 */
	private static void close(ObjectContainer db) {
		if (db != null) db.close();
	}
}
