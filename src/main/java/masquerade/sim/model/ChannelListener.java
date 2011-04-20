package masquerade.sim.model;


/**
 * A listener receiving requests on a channel
 * @param <T> Channel configuration type for this listener 
 */
public interface ChannelListener<T extends Channel> {
	
	/**
	 * Starts the channel listener using a {@link Channel} configuration
	 * @param channel
	 * @param simulationRunner
	 */
	void start(T channel, SimulationRunner simulationRunner, ChannelListenerContext context);

	/**
	 * Stops this channel listener
	 */
	void stop(ChannelListenerContext context);
}
