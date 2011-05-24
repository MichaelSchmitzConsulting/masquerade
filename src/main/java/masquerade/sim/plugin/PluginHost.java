package masquerade.sim.plugin;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class PluginHost {
	private Felix m_felix = null;

	public void start() {
		// Create a configuration property map.
		Map<String, Object> config = new HashMap<String, Object>();

		try {
			// Now create an instance of the framework with
			// our configuration properties.
			m_felix = new Felix(config);

			// Now start Felix instance.
			m_felix.start();
		} catch (Exception ex) {
			System.err.println("Could not create framework: " + ex);
			ex.printStackTrace();
		}
	}

	public Bundle[] getInstalledBundles() {
		return m_felix.getBundleContext().getBundles();
	}

	public void stop() throws BundleException, InterruptedException {
		// Shut down the felix framework when stopping the
		// host application.
		m_felix.stop();
		m_felix.waitForStop(0);
	}
}