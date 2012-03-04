package masquerade.sim.plugin;

/**
 * Exception thrown when installation or lifecycle management 
 * functions fail.
 */
public class PluginException extends Exception {
	private static final long serialVersionUID = 1L;

	public PluginException(String msg, Throwable t) {
		super(msg, t);
	}

	public PluginException(String msg) {
		super(msg);
	}
}
