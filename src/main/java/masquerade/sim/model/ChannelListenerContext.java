package masquerade.sim.model;


/**
 * Context object for channel listeners, allowing to share information
 * between channel listeners.
 */
public interface ChannelListenerContext {
	void setAttribute(String name, Object value);
	<T> T getAttribute(String name);
	void removeAttribute(String name);
	
	/**
	 * Provides access to a {@link VariableHolder} with configuration
	 * variables to substitute in environment-specific channel
	 * listener configurations settings such as URIs, ports etc.
	 * @return A configuration {@link VariableHolder}
	 */
	VariableHolder getVariableHolder();
}
