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
import masquerade.sim.db.ModelRepository;
import masquerade.sim.db.ModelRepositoryFactory;
import masquerade.sim.db.ModelRepositorySessionFactory;
import masquerade.sim.db.RequestHistorySessionFactory;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.impl.FileLoaderImpl;
import masquerade.sim.model.impl.SimulationRunnerImpl;

import org.apache.commons.io.FileUtils;

import com.db4o.ObjectContainer;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;

public class ApplicationLifecycle implements ServletContextListener {

	private static final String SERVLET_WORK_DIR = "javax.servlet.context.tempdir";
	private static final String CONTEXT = "_masqApplicationContext";

	public static ApplicationContext getApplicationContext(ServletContext context) {
		return (ApplicationContext) context.getAttribute(CONTEXT);
	}

	public static ApplicationContext getApplicationContext(Application app) {
		WebApplicationContext web = (WebApplicationContext) app.getContext();
		return (ApplicationContext) web.getHttpSession().getServletContext().getAttribute(CONTEXT);
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ObjectContainer db = null;
		try {
			ServletContext servletContext = event.getServletContext();
			servletContext.log("Starting masquerade simulator");
			
			// Determine db file location
			File dbFile = getDbFileLocation(servletContext);
			servletContext.log("Database location: " + dbFile.getAbsolutePath());
			
			// Create database
			DatabaseLifecycle databaseLifecycle = new DatabaseLifecycle();
			db = databaseLifecycle.start(dbFile);
			
			File requestLogDir = getRequestLogDir(servletContext);
			servletContext.log("Request log dir: " + requestLogDir.getAbsolutePath());
			
			// Create request history factory
			RequestHistoryFactory requestHistoryFactory = new RequestHistorySessionFactory(db, requestLogDir);
			
			// Create converter
			Converter converter = new CompoundConverter();
			
			// Create file loader
			FileLoader fileLoader = createFileLoader(servletContext);
			
			// Create simulation runner
			SimulationRunner simulationRunner = new SimulationRunnerImpl(requestHistoryFactory, converter, fileLoader);
			
			// Create channel listener registry
			ChannelListenerRegistry listenerRegistry = new ChannelListenerRegistryImpl(simulationRunner);

			// Create model repository factory
			ModelRepositoryFactory modelRepositoryFactory = new ModelRepositorySessionFactory(db);
			
			// Add channel change trigger
			registerChannelChangeTrigger(db, listenerRegistry);
			
			// Create application context
			ApplicationContext applicationContext = new ApplicationContext(databaseLifecycle, listenerRegistry, requestHistoryFactory, modelRepositoryFactory,
				fileLoader, converter);
			
			// Save application context reference in servlet context
			servletContext.setAttribute(CONTEXT, applicationContext);
			
			// Start channels
			ModelRepository repo = applicationContext.getModelRepositoryFactory().startModelRepositorySession();
			try {
				for (Channel channel : repo.getChannels()) {
					listenerRegistry.notifyChannelChanged(channel);
				}
			} finally {
				repo.endSession();
			}
			
			servletContext.log("Simulator started");
		} catch (IOException ex) {
			close(db);
			throw new IllegalArgumentException("Cannot create dir", ex);
		} catch (RuntimeException t) {
			close(db);
			throw t;
		}
	}

	private FileLoader createFileLoader(ServletContext servletContext) throws IOException {
		return new FileLoaderImpl(getArtifactsDir(servletContext));
	}

	private void registerChannelChangeTrigger(ObjectContainer db, ChannelListenerRegistry channelListenerRegistry) {
		EventRegistry events = EventRegistryFactory.forObjectContainer(db);
		events.committed().addListener(new ChannelChangeTrigger(channelListenerRegistry));
	}

	private void close(ObjectContainer db) {
		if (db != null) db.close();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		servletContext.log("Shutting down masquerade simulator");
		ApplicationContext context = (ApplicationContext) servletContext.getAttribute(CONTEXT);
		if (context != null) {
			context.getDb().stop();
			servletContext.removeAttribute(CONTEXT);
		}
	}
	
	private File getDbFileLocation(ServletContext servletContext) {
		String dbFileLocation = System.getProperty("masquerade.db.file.location");
		if (dbFileLocation == null) {
			File workDir = getWorkDir(servletContext);
			File dbFile = new File(workDir, getAppName(servletContext) + "-db.db4o");
			return dbFile;
		} else {
			return new File(dbFileLocation);
		}
	}
	
	private static File getRequestLogDir(ServletContext servletContext) throws IOException {
		String requestLogDir = System.getProperty("masquerade.request.log.dir");
		File dir;
		if (requestLogDir == null) {
			dir = servletWorkDir(servletContext, "-requestLog");
		} else {
			File requestLog = new File(requestLogDir);
			FileUtils.forceMkdir(requestLog.getParentFile());
			dir = requestLog;
		}
		return createDir(dir);
	}
	
	private static File getArtifactsDir(ServletContext servletContext) throws IOException {		
		String artifactDir = System.getProperty("masquerade.artifact.dir");
		File dir;
		if (artifactDir == null) {
			dir = servletWorkDir(servletContext, "-artifact");
		} else {
			dir = new File(artifactDir);
		}
		return createDir(dir);
	}
	
	private static File servletWorkDir(ServletContext servletContext, String subdir) {
		File dir;
		String name = getAppName(servletContext);
		File workDir = getWorkDir(servletContext);
		dir = new File(workDir, name + subdir);
		return dir;
	}
	
	private static File createDir(File dir) throws IOException {
		FileUtils.forceMkdir(dir);
		return dir;
	}
	
	private static String getAppName(ServletContext servletContext) {
		String name = servletContext.getContextPath().replace("/", "_");
		return name.length() > 0 ? name.substring(1) : name; // Remove leading _
	}

	private static File getWorkDir(ServletContext servletContext) {
		File workDir = (File) servletContext.getAttribute(SERVLET_WORK_DIR);
		return workDir;
	}
}
