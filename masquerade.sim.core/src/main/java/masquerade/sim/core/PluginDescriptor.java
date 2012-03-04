package masquerade.sim.core;

import java.io.File;
import java.util.Collection;

/**
 * 
 */
public interface PluginDescriptor {
	
	/**
	 * @param pluginFileName
	 */
	void notifyInstalled(String pluginFileName);

	/**
	 * @param name
	 */
	void notifyUninstalled(String pluginFileName);

	/**
	 * @return
	 */
	Collection<File> listPlugins();
}
