package masquerade.sim.app.ui.factory;

import masquerade.sim.model.Channel;

/**
 * Interface for UI components allowing the user to create {@link Channel}s
 */
public interface ChannelFactory {

	void createChannel(ChannelFactoryCallback createCallback);
	
	interface ChannelFactoryCallback {
		void onCreate(Channel channel);
	}
}
