package masquerade.sim.webapp;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.felix.http.proxy.ProxyServlet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Installs and starts all bundles contained in this webapp
 */
public final class WebappBundlesActivator implements BundleActivator {
	private static final String BUNDLE_EXTENSION = ".jar";
	private static final String BUNDLE_DIRECTORY = "/WEB-INF/bundles/";
	
	private final ServletContext servletContext;

	public WebappBundlesActivator(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Installs and starts all bundles contained in the WAR under 
	 * <code>/WEB-INF/bundles/*.jar</code>. Sets the {@link BundleContext}
	 * as a {@link ServletContext} attribute (required by {@link ProxyServlet}.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Collection<Bundle> bundles = new ArrayList<Bundle>();
		// Install all bundles
		for (URL url : getPackagedBundles()) {
			Bundle bundle = context.installBundle(url.toExternalForm());
			bundles.add(bundle);
		}

		// Start all bundles
		for (Bundle bundle : bundles) {
			bundle.start();
		}
		
		// Set BundleContext as attribute on ServletContext
		servletContext.setAttribute(BundleContext.class.getName(), context);
	}

	/**
	 * Nothing to do, stopping the OSGi framework is handled
	 * by the servlet context listener which also uninstalls
	 * and stops all bundles.
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
	}

	/**
	 * @return All bundles contained in the WAR
	 * @throws Exception
	 */
	private Collection<URL> getPackagedBundles() throws Exception {
		@SuppressWarnings("unchecked")
		Collection<String> resources = servletContext.getResourcePaths(BUNDLE_DIRECTORY);
		
		Collection<URL> bundles = new ArrayList<URL>();
		for (String name : resources) {
			if (name.endsWith(BUNDLE_EXTENSION)) {
				bundles.add(servletContext.getResource(name));
			}
		}

		return bundles;
	}
}
