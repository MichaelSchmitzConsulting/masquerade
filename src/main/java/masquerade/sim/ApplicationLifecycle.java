package masquerade.sim;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import masquerade.sim.db.DatabaseLifecycle;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Channel;

import org.apache.commons.io.FileUtils;

import com.db4o.ObjectContainer;
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
			
			File dbFile = getDbFileLocation(servletContext);
			servletContext.log("Database location: " + dbFile.getAbsolutePath());
			
			// Create database
			DatabaseLifecycle databaseLifecycle = new DatabaseLifecycle();
			db = databaseLifecycle.start(dbFile);
			
			File requestLogDir = getRequestLogDir(servletContext);
			servletContext.log("Request log dir: " + requestLogDir.getAbsolutePath());
			
			// Start channels
			ApplicationContext applicationContext = new ApplicationContext(databaseLifecycle, requestLogDir);
			ModelRepository repo = applicationContext.startModelRepositorySession();
			try {
				RequestHistoryFactory requestHistoryFactory = new RequestHistorySessionFactory(applicationContext);
				List<Channel> channels = repo.getChannels();
				for (Channel channel : channels) {
					channel.start(requestHistoryFactory);
					// Hold a strong ref to all started channels in the app context
					// Otherwise the DB will recreate channels when garbage collected,
					// and their transient attributes will be lost (requestHistoryFactory)
					// Channels must exist only once as their lifecycle is application-scoped. TODO: Split channels into config/logic
					// to avoid using a global session on the DB to get the same channel instances in all of the app.
					applicationContext.addChannel(channel);
				}
			} finally {
				repo.endSession();
			}
			
			// Save application context reference in servlet context
			servletContext.setAttribute(CONTEXT, applicationContext);
		} catch (IOException ex) {
			close(db);
			throw new IllegalArgumentException("Cannot create dir", ex);
		} catch (RuntimeException t) {
			close(db);
			throw t;
		}
	}

	private void close(ObjectContainer db) {
		if (db != null) db.close();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		ApplicationContext context = (ApplicationContext) servletContext.getAttribute(CONTEXT);
		servletContext.log("Shutting down masquerade simulator");
		context.getDb().stop();
		servletContext.removeAttribute(CONTEXT);
	}
	
	private File getDbFileLocation(ServletContext servletContext) {
		String dbFileLocation = System.getProperty("masquerade.db.file.location");
		if (dbFileLocation == null) {
			File workDir = getWorkDir(servletContext);
			File dbFile = new File(workDir, getAppName(servletContext) + "Db.db4o");
			return dbFile;
		} else {
			return new File(dbFileLocation);
		}
	}
	
	private static File getRequestLogDir(ServletContext servletContext) throws IOException {
		String requestLogDir = System.getProperty("masquerade.request.log.dir");
		if (requestLogDir == null) {
			String name = getAppName(servletContext);
			File workDir = getWorkDir(servletContext);
			return new File(workDir, name + "RequestLog");
		} else {
			File requestLog = new File(requestLogDir);
			FileUtils.forceMkdir(requestLog.getParentFile());
			return requestLog;
		}
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
