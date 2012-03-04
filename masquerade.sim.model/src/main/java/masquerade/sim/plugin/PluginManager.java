package masquerade.sim.plugin;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

/**
 * Manages plugins in Masquerade. Allows to install, start, stop and uninstall plugins.
 */
public interface PluginManager {
	/**
	 * List all available plugins
	 * @return
	 */
	Collection<Plugin> listPlugins();

	/**
	 * Installs and starts new plugin
	 * @param url
	 * @return The plugin's unique identifier
	 */
	Plugin installPlugin(URL url) throws PluginException;

	/**
	 * Installs and starts new plugin
	 * @param fileName Name of the plugin bundle file
	 * @param inputStream stream to read the plugin bundle from
	 * @return The plugin's unique identifier
	 */
	Plugin installPlugin(String name, InputStream inputStream) throws PluginException;

	/**
	 * @param pluginName
	 * @param pluginVersion
	 * @return 
	 */
	Plugin getPlugin(String pluginName, String pluginVersion);
}
