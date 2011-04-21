package masquerade.sim.model;

/**
 * Context object for channel listeners, allowing to share information
 * between channel listeners.
 */
public interface ChannelListenerContext {
	void setAttribute(String name, Object value);
	<T> T getAttribute(String name);
	void removeAttribute(String name);
}
