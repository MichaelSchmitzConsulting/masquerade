package masquerade.sim.core.repository;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.impl.AbstractChannel;

public class ChannelStub extends AbstractChannel {

	public ChannelStub(String name) {
		super(name);
	}
	
	@Override
	public Class<? extends ChannelListener<? extends Channel>> listenerType() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isActive() {
		throw new UnsupportedOperationException();
	}
}
