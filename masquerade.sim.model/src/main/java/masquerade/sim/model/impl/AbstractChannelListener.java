package masquerade.sim.model.impl;

import java.util.Date;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.ResponseDestination;
import masquerade.sim.model.SimulationRunner;

/**
 * Base class for {@link ChannelListener channel listeners, provides a SimulationRunner and keeps
 * track of request mappings.
 * @param <T>
 */
public abstract class AbstractChannelListener<T extends Channel> implements ChannelListener<T>, RequestProcessor {
	private SimulationRunner simulationRunner;
	private String channelId;
	
	@Override
	public final void start(T channel, SimulationRunner simulationRunner, ChannelListenerContext context) {
		this.simulationRunner = simulationRunner;
		this.channelId = channel.getId();
		
		onStart(channel, context);
	}

	@Override
	public final void stop(ChannelListenerContext context) {
		onStop(context);
		this.simulationRunner = null;
		this.channelId = null;
	}
	
	protected abstract void onStart(T channel, ChannelListenerContext channelListenerContext);
	protected abstract void onStop(ChannelListenerContext channelListenerContext); 
	
	@Override
	public void processRequest(String clientInfo,  Object request, ResponseDestination responseDestination, Date requestTimestamp) throws Exception {
		simulationRunner.runSimulation(responseDestination, channelId, clientInfo, request, requestTimestamp);
	}
}
