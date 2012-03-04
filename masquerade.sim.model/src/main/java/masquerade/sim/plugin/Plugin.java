package masquerade.sim.plugin;

/**
 * Provides access to the properties of an installed plugin
 */
public interface Plugin {
	enum State { STARTED, STOPPED };
	
	/**
	 * Returns a unique name for a plugin. Different versions of the
	 * same plugin are not supported.
	 * 
	 * @return Plugin identifier
	 */
	String getIdentifier();
	
	/**
	 * Textual description of the plugin
	 * 
	 * @return Plugin description if provided, or the empty string
	 */
	String getDescription();
	
	/**
	 * Plugin version (major.minor.micro[.qualifier])
	 * 
	 * @return Plugin version
	 */
	String getVersion();
	
	/**
	 * @return The {@link State} this plugin is currently in
	 */
	State getState();
	
	/**
	 * Starts a plugin
	 * 
	 * @throws PluginException
	 */
	void start() throws PluginException;
	
	/**
	 * Stops a plugin, leaving it installed
	 * 
	 * @throws PluginException
	 */
	void stop() throws PluginException;
	
	/**
	 * Deletes a plugin. An attempt to stop the plugin is 
	 * made if currently in {@link State#STARTED}.
	 * 
	 * @throws PluginException
	 */
	void remove() throws PluginException;
}
