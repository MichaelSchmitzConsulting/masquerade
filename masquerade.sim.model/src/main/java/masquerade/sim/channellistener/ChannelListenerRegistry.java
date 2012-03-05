package masquerade.sim.channellistener;

import java.util.Collection;

import masquerade.sim.model.ChannelListener;

/**
 * A registry for {@link ChannelListener} instances. Manages
 * lifecycle for these listeners.
 */
public interface ChannelListenerRegistry {
	/**
	 * Get a {@link ChannelListener} by name
	 * @param <T> Expected channel listener type
	 * @param name Channel name
	 * @param channelListenerType Expected channel listener type
	 * @return Listener instance, or <code>null</code> if not found
	 */
	<T extends ChannelListener<?>> T getChannelListener(String name, Class<T> channelListenerType);
	
	/**
	 * Get all {@link ChannelListener} of a given type
	 * @param <T> Channel listener type
	 * @param channelListenerTye Channel listener class
	 * @return {@link Collection} with all listener instances of the given type
	 */
	<T extends ChannelListener<?>> Collection<T> getAllListeners(Class<T> channelListenerTye);
	
	/**
	 * Notify registry to stop and remove a channel listener
	 * @param name Channel name to be stopped
	 */	
	void stop(String name);

	/**
	 * Restarts or starts an updated channel
	 * @param name
	 */
	void startOrRestart(String name);
	
	/**
	 * Starts listeners for all channels
	 */
	void startAll();
	
	/**
	 * Stops and removes all channel listeners in this registry
	 */
	void stopAll();

}
