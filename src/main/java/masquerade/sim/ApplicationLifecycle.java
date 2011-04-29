package masquerade.sim;

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
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.impl.FileLoaderImpl;
import masquerade.sim.model.impl.SimulationRunnerImpl;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.FileUtils;

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

	private static final int MINUTE = 60 * 1000;
	// TODO: Move these 2 values to settings
	private static final long cleanupSleepPeriodMs = 10 * MINUTE;
	private static final int requestsToKeepInHistory = 100;

	private static final String MSQ_WORKSUBDIR = ".masquerade";

	private static final StatusLog log = StatusLogger.get(ApplicationLifecycle.class);
	
	private static final String SERVLET_WORK_DIR = "javax.servlet.context.tempdir";
	private static final String CONTEXT = "_masqApplicationContext";


	/**
	 * Retrieves the current {@link ApplicationContext} for a webapp as stored
	 * in the {@link ServletContext} as an attribute.
	 * 
	 * @param context {@link ServletContext}
	 * @return The {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext(ServletContext context) {
		return (ApplicationContext) context.getAttribute(CONTEXT);
	}

	/**
	 * Retrieves the current {@link ApplicationContext} for a Vaadin application
	 * 
	 * @param app Vaadin application
	 * @return The {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext(Application app) {
		WebApplicationContext web = (WebApplicationContext) app.getContext();
		return (ApplicationContext) web.getHttpSession().getServletContext().getAttribute(CONTEXT);
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
			File modelDbFile = getDbFileLocation(servletContext, false);
			File historyDbFile = getDbFileLocation(servletContext, true);
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
			
			// Create simulation runner
			SimulationRunner simulationRunner = new SimulationRunnerImpl(requestHistoryFactory, converter, fileLoader, namespaceResolver);
			
			// Create channel listener registry
			ChannelListenerRegistry listenerRegistry = new ChannelListenerRegistryImpl(simulationRunner);
			
			// Add channel change trigger
			registerChannelChangeTrigger(modelDb, listenerRegistry);
			
			// Create history cleanup job
			RequestHistoryCleanupJob cleanupJob = new RequestHistoryCleanupJob(requestHistoryFactory, cleanupSleepPeriodMs, requestsToKeepInHistory);
			
			// Create application context
			ApplicationContext applicationContext = new ApplicationContext(modelDbLifecycle, historyDbLifecycle, listenerRegistry, requestHistoryFactory, 
					modelRepositoryFactory, fileLoader, converter, artifactsRoot, namespaceResolver, cleanupJob);
			
			// Save application context reference in servlet context
			servletContext.setAttribute(CONTEXT, applicationContext);
			
			// Start channels
			ModelRepository repo = applicationContext.getModelRepositoryFactory().startModelRepositorySession();
			try {
				listenerRegistry.startAll(repo.getChannels());
			} finally {
				repo.endSession();
			}
			
			// Start request history cleanup job
			cleanupJob.start();
			
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
		ApplicationContext context = (ApplicationContext) servletContext.getAttribute(CONTEXT);
		if (context != null) {
			context.getRequestHistoryCleanupJob().stop();
			stopChannels(context.getChannelListenerRegistry());
			context.getModelDb().stop();
			context.getHistoryDb().stop();
			servletContext.removeAttribute(CONTEXT);
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

	/**
	 * Reads the masquerade reuest log settings from the system property
	 * <code>masquerade.request.log.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param servletContext {@link ServletContext}
	 * @param postfix 
	 * @return Where the masquerade database should be located
	 */
	private static File getDbFileLocation(ServletContext servletContext, boolean isRequestLog) {
		String dbFileLocation;
		
		if (isRequestLog) {
			dbFileLocation = System.getProperty("masquerade.db.history.file.location");
		} else {
			dbFileLocation = System.getProperty("masquerade.db.file.location");
		}
		
		if (dbFileLocation == null) {
			File workDir = getWorkDir(servletContext);
			String postfix = isRequestLog ? "-history" : "";
			File dbFile = new File(workDir, getAppName(servletContext) + postfix + "-db.db4o");
			return dbFile;
		} else {
			return new File(dbFileLocation);
		}
	}
	
	/**
	 * Reads the masquerade reuest log location setting from the system property
	 * <code>masquerade.request.log.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param servletContext {@link ServletContext}
	 * @return Where the masquerade request log directory should be located
	 */
	private static File getRequestLogDir(ServletContext servletContext) throws IOException {
		String requestLogDir = System.getProperty("masquerade.request.log.dir");
		File dir;
		if (requestLogDir == null) {
			dir = getWorkSubDir(servletContext, "-requestLog");
		} else {
			File requestLog = new File(requestLogDir);
			FileUtils.forceMkdir(requestLog.getParentFile());
			dir = requestLog;
		}
		FileUtils.forceMkdir(dir);
		return dir;
	}
	
	/**
	 * Reads the artifact directory location setting from the system property
	 * <code>masquerade.artifact.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param servletContext {@link ServletContext}
	 * @return Where the masquerade request log directory should be located
	 */
	private static File getArtifactsDir(ServletContext servletContext) throws IOException {		
		String artifactDir = System.getProperty("masquerade.artifact.dir");
		File dir;
		if (artifactDir == null) {
			dir = getWorkSubDir(servletContext, "-artifact");
		} else {
			dir = new File(artifactDir);
		}
		FileUtils.forceMkdir(dir);
		return dir;
	}
	
	/**
	 * @param servletContext
	 * @param subdir
	 * @return A {@link File} for the specified subdir in the webapp's working directory
	 */
	private static File getWorkSubDir(ServletContext servletContext, String subdir) {
		File dir;
		String name = getAppName(servletContext);
		File workDir = getWorkDir(servletContext);
		dir = new File(workDir, name + subdir);
		return dir;
	}
	
	/**
	 * @param servletContext
	 * @return The name of the webapp
	 */
	private static String getAppName(ServletContext servletContext) {
		String name = servletContext.getContextPath().replace("/", "_");
		return name.length() > 0 ? name.substring(1) : "masquerade"; // Remove leading _, set name if deployed at root
	}

	/**
	 * The webapp's working directory as determined by the servlet context
	 * attribute <code>javax.servlet.context.tempdir</code>.
	 * 
	 * @param servletContext
	 * @return Location of the working directory
	 */
	private static File getWorkDir(ServletContext servletContext) {
		File userDir = FileUtils.getUserDirectory();
		File workDir = new File(userDir, MSQ_WORKSUBDIR);
		if (!userDir.exists() || (!workDir.exists() && !workDir.mkdirs())) {
			File servletDir = (File) servletContext.getAttribute(SERVLET_WORK_DIR);
			workDir = new File(servletDir, MSQ_WORKSUBDIR);
			if (!workDir.exists() && !workDir.mkdirs()) {
				throw new IllegalStateException("Cannot create work directory in user.home or javax.servlet.context.tempdir");
			}
		}
		
		return workDir;
	}
}
