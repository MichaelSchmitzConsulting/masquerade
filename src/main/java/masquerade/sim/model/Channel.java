package masquerade.sim.model;

import java.util.Set;

/**
 * A channel receives requests
 */
public interface Channel {
	/**
	 * @return Unique channel name
	 */
	String getName();
	
	/**
	 * @return Channel description
	 */
	String getDescription();
	
	/**
	 * @return All {@link RequestMapping mappings} for this channel
	 */
	Set<RequestMapping<?>> getRequestMappings();

	void setRequestMappings(Set<RequestMapping<?>> requestMappings);
	
	Class<? extends ChannelListener<? extends Channel>> getListenerType();
}
