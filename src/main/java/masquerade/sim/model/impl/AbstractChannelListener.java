package masquerade.sim.model.impl;

import java.io.OutputStream;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.SimulationRunner;

public abstract class AbstractChannelListener<T extends Channel> implements ChannelListener<T> {
	private SimulationRunner simulationRunner;
	private String channelName;

	@Override
	public final void start(T channel, SimulationRunner simulationRunner) {
		this.simulationRunner = simulationRunner;
		channelName = channel.getName();
		onStart(channel);
	}

	@Override
	public final void stop() {
		onStop();
	}
	
	protected abstract void onStart(T channel);
	protected abstract void onStop(); 

	protected void processRequest(Set<RequestMapping<?>> requestMappings, String clientInfo,  Object request, OutputStream responseOutput) throws Exception {
		simulationRunner.runSimulation(responseOutput, channelName, clientInfo, requestMappings, request);
	}
}
