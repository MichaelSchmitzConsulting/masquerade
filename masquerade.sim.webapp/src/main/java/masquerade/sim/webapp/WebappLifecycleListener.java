package masquerade.sim.webapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.osgi.framework.Constants;

/**
 * Starts/stops the OSGi framework as the webapp is started and stopped.
 */
public final class WebappLifecycleListener implements ServletContextListener {
	// Matches properties in ConfigurationImpl, which is not available on the webapps
	// classpath as it is a bundle
	private static final String ARTIFACT_ROOT_LOCATION = "masquerade.configuration.artifactRootLocation";
	private static final String MODEL_DB_FILE_LOCATION = "masquerade.configuration.modelDbFileLocation";
	private static final String HISTORY_DB_FILE_LOCATION = "masquerade.configuration.historyDbFileLocation";
	private static final String REQUEST_LOG_DIR = "masquerade.configuration.requestLogDir";
	private static final String PLUGIN_LOCATION = "masquerade.configuration.pluginLocation";
	private static final String MASQUERADE_VERSION = "masquerade.version";
	private static final String MASQUERADE_BUILD_TIMESTAMP = "masquerade.build.timestamp";

	private OsgiFrameworkLifecycle service;

	/**
	 * Start OSGi framework
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		String appDirName = getAppDirName(servletContext);
		
		HomeResolver resolver = new HomeResolver(appDirName);
		
		// Determine location of bundle cache directory
		File bundleCacheDir = getBundleCacheDir(resolver);

		// Start OSGi framework
		service = new OsgiFrameworkLifecycle(servletContext);
		service.setProperty(Constants.FRAMEWORK_STORAGE, bundleCacheDir.getAbsolutePath());
		service.setProperty(Constants.FRAMEWORK_STORAGE_CLEAN, Constants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
		
		// Set location configuration properties 
		try {
			service.setProperty(ARTIFACT_ROOT_LOCATION, resolver.getArtifactRootLocation().getAbsolutePath());
			service.setProperty(MODEL_DB_FILE_LOCATION, resolver.getDbFileLocation(DbType.MODEL).getAbsolutePath());
			service.setProperty(HISTORY_DB_FILE_LOCATION, resolver.getDbFileLocation(DbType.HISTORY).getAbsolutePath());
			service.setProperty(REQUEST_LOG_DIR, resolver.getRequestLogDir().getAbsolutePath());
			service.setProperty(PLUGIN_LOCATION, resolver.getPluginLocation().getAbsolutePath());
		} catch (IOException ex) {
			throw new IllegalArgumentException("Unable to access a configured directory", ex);
		}
		
		// Set version property
		InputStream is = getClass().getResourceAsStream("masquerade.properties");
		Properties props = new Properties();
		if (is != null) { 
			try {
				props.load(is);
			} catch (IOException e) {
				servletContext.log("Unable to load version/build information", e);
			}
		} else {
			servletContext.log("Warning: masquerade.properties not found");
		}
		service.setProperty(MASQUERADE_VERSION, props.getProperty(MASQUERADE_VERSION, "n/a"));
		service.setProperty(MASQUERADE_BUILD_TIMESTAMP, props.getProperty(MASQUERADE_BUILD_TIMESTAMP, "n/a"));
				
		service.start();
	}

	private static File getBundleCacheDir(HomeResolver resolver) {
		File bundleCacheDir;
		try {
			bundleCacheDir = resolver.getPludingBundleCacheDir();
		} catch (IOException e) {
			throw new IllegalArgumentException("Error creating/accessing bundle cache dir", e);
		}
		return bundleCacheDir;
	}

	/**
	 * Stop OSGi framework
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		service.stop();
	}

	/**
	 * @param servletContext
	 * @return The context path of the webapp suitable for use as a directory name 
	 */
	private static String getAppDirName(ServletContext servletContext) {
		String name = servletContext.getContextPath().replace("/", "_").replace(":", "_").replace("\\", "_");
		return name.length() > 0 ? name.substring(1) : "masquerade"; // Remove leading _, set name if deployed at root
	}
}
