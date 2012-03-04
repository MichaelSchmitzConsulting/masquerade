package masquerade.sim.webapp;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Responsible for starting/stopping the OSGi framework and all
 * included bundles for this webapp.
 */
public final class OsgiFrameworkLifecycle {
	private final ServletContext servletContext;
	private Felix felix;
	private Map<String, Object> extraProperties = new HashMap<String, Object>();

	public OsgiFrameworkLifecycle(ServletContext context) {
		this.servletContext = context;
	}
	
	/**
	 * Sets an extra framework property available to all bundles using
	 * {@link BundleContext#getProperty(String)}.
	 *  
	 * @param name
	 * @param value
	 */
	public void setProperty(String name, Object value) {
		extraProperties.put(name, value);
	}

	/**
	 * Starts the OSGi framework and all included bundles
	 */
	public void start() {
		try {
			doStart();
		} catch (Exception e) {
			error("Failed to start OSGi framework", e);
		}
	}

	/**
	 * Stops the OSGi framework
	 */
	public void stop() {
		try {
			doStop();
		} catch (Exception e) {
			error("Failed to stop OSGi framework", e);
		}
	}
	
	private void doStart() throws BundleException, IOException {
		WebappBundlesActivator activator = new WebappBundlesActivator(servletContext);
		
		Map<Object, Object> configuration = getConfiguration(activator);
		Felix felix = new Felix(configuration);
		felix.start();
		
		this.felix = felix;
		
		info("OSGi framework startup complete");
	}

	private void doStop() throws BundleException, InterruptedException {
		if (felix != null) {
			felix.stop();
			felix.waitForStop(30000);
		}

		info("OSGi framework shutdown complete");
	}

	/**
	 * Creates the OSGi framework configuration
	 * 
	 * @param activator Activator for the system bundle
	 * @return Configuration to create an OSGi framework instance with
	 * @throws IOException If /WEB-INF/osgi.properties cannot be read
	 */
	private Map<Object, Object> getConfiguration(BundleActivator activator) throws IOException {
		// Load OSGi framework properties
		Properties map = new Properties();
		map.load(servletContext.getResourceAsStream("/WEB-INF/osgi.properties"));

		// Create a new activator
		map.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP,
				Collections.singletonList(activator));
		
		map.putAll(extraProperties);

		return map;
	}

	private void info(String message) {
		servletContext.log(message);
	}

	private void error(String message, Throwable cause) {
		servletContext.log(message, cause);
	}
}
