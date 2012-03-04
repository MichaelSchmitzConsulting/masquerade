package masquerade.sim.core;

import masquerade.sim.plugin.PluginException;
import masquerade.sim.plugin.PluginManager;

/**
 * Core-internal {@link PluginManager} extensions that allows to 
 * control {@link BundlePlugin} lifecycle
 */
public interface InternalPluginManager extends PluginManager {

	void initialize();
	
	void shutdown();
	
	void startPlugin(BundlePlugin plugin) throws PluginException;

	void stopPlugin(BundlePlugin plugin) throws PluginException;

	void removePlugin(BundlePlugin plugin) throws PluginException;
}
