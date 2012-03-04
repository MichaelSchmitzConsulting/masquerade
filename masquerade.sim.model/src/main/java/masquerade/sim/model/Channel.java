package masquerade.sim.model;

/**
 * A channel receives requests
 */
public interface Channel extends Named {
	/**
	 * @return Unique channel name
	 */
	@Override
	String getName();
	
	/**
	 * @return Channel description
	 */
	String getDescription();
	
	boolean isActive();
	
	Class<? extends ChannelListener<? extends Channel>> listenerType();
}
