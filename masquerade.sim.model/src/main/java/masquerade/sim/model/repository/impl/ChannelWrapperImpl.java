package masquerade.sim.model.repository.impl;

import masquerade.sim.model.Channel;
import masquerade.sim.model.repository.ChannelWrapper;

public class ChannelWrapperImpl implements ChannelWrapper {

	private final Channel channel;
	private final boolean isPersistent;

	public ChannelWrapperImpl(Channel channel, boolean isPersistent) {
		this.channel = channel;
		this.isPersistent = isPersistent;
	}

	@Override
	public Channel getChannel() {
		return channel;
	}

	@Override
	public boolean isPersistent() {
		return isPersistent;
	}
}
