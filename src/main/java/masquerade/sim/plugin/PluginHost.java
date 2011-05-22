package masquerade.sim.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

public class HostApplication {
	private HostActivator m_activator = null;
	private Felix m_felix = null;
	private ServiceTracker m_tracker;

	public void start() {
		// Create a configuration property map.
		Map<String, Object> config = new HashMap<String, Object>();

		Map<String, Object> lookupMap = new HashMap<String, Object>();
		lookupMap.put("key", "someValue");

		// Create host activator;
		m_activator = new HostActivator(lookupMap);

		List<BundleActivator> list = new ArrayList<BundleActivator>();
		list.add(m_activator);
		config.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

		config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "com.maettu.osgi.felix.lookup; version=1.0.0");

		try {
			// Now create an instance of the framework with
			// our configuration properties.
			m_felix = new Felix(config);

			// Now start Felix instance.
			m_felix.start();
			
			m_felix.getBundleContext().installBundle("file:bundle/org.apache.felix.gogo.runtime-0.8.0.jar");
			m_felix.getBundleContext().installBundle("file:bundle/org.apache.felix.gogo.shell-0.8.0.jar");
			m_felix.getBundleContext().installBundle("file:bundle/org.apache.felix.gogo.command-0.8.0.jar");
			m_felix.getBundleContext().installBundle("file:bundle/org.apache.felix.bundlerepository-1.6.2.jar");

		} catch (Exception ex) {
			System.err.println("Could not create framework: " + ex);
			ex.printStackTrace();
		}

		m_tracker = new ServiceTracker(m_activator.getContext(), Lookup.class.getName(), null);
		m_tracker.open();
	}

	public Bundle[] getInstalledBundles() {
		// Use the system bundle activator to gain external
		// access to the set of installed bundles.
		return m_activator.getBundles();
	}

	public Collection<Lookup> getLookupServices() {
		Collection<Lookup> services = new ArrayList<Lookup>();
		
		for (Object service : m_tracker.getServices()) {
			services.add((Lookup) service);
		}
		
		return services;
	}
	
	public void shutdownApplication() throws BundleException,
			InterruptedException {
		// Shut down the felix framework when stopping the
		// host application.
		m_felix.stop();
		m_felix.waitForStop(0);
	}
}