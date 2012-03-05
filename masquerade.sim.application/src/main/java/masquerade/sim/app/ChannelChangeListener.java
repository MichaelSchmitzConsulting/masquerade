package masquerade.sim.app;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.listener.UpdateListener;

/**
 * Handles changes to {@link Channel} objects by starting/stopping them if required
 */
public class ChannelChangeListener implements CreateListener, UpdateListener, DeleteListener {

	private final ChannelListenerRegistry channelListenerRegistry;

	public ChannelChangeListener(ChannelListenerRegistry channelListenerRegistry) {
		this.channelListenerRegistry = channelListenerRegistry;
	}

	@Override
	public void notifyDelete(Object obj) {
		if (obj instanceof Channel) {
			Channel channel = (Channel) obj;
			channelListenerRegistry.stop(channel.getName());
		}
	}

	@Override
	public void notifyUpdated(Object obj) {
		if (obj instanceof Channel) {
			Channel channel = (Channel) obj;
			channelListenerRegistry.startOrRestart(channel.getName());
		}
	}

	@Override
	public void notifyCreate(Object obj) {
		if (obj instanceof Channel) {
			Channel channel = (Channel) obj;
			channelListenerRegistry.startOrRestart(channel.getName());
		}
	}
}
