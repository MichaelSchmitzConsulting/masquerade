package masquerade.sim.model;

/**
 * A channel receives requests
 */
public interface Channel {
	/**
	 * @return Unique channel name
	 */
	String getId();
	
	/**
	 * @return Channel description
	 */
	String getDescription();
	
	boolean isActive();
	
	Class<? extends ChannelListener<? extends Channel>> listenerType();
}
