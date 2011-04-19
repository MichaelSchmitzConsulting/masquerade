package masquerade.sim.channel;

import java.util.Collection;
import java.util.List;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;

public interface ChannelListenerRegistry {
	<T extends ChannelListener<?>> T getChannelListener(String name, Class<T> channelListenerType);
	
	<T extends ChannelListener<?>> Collection<T> getAllListeners(Class<T> channelListenerTye);
	
	void notifyChannelChanged(Channel channel);
	void notifyChannelDeleted(String name);
	
	void startAll(List<Channel> list);
	void stopAll();
}
