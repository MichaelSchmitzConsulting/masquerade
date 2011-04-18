package masquerade.sim.model.impl;

import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.SimulationRunner;

/**
 * Base class for {@link ChannelListener channel listeners, provides a SimulationRunner and keeps
 * track of request mappings.
 * @param <T>
 */
public abstract class AbstractChannelListener<T extends Channel> implements ChannelListener<T>, RequestProcessor {
	private SimulationRunner simulationRunner;
	private String channelName;
	private Set<RequestMapping<?>> requestMappings;

	@Override
	public final void start(T channel, SimulationRunner simulationRunner) {
		this.simulationRunner = simulationRunner;
		channelName = channel.getName();
		requestMappings = new LinkedHashSet<RequestMapping<?>>(channel.getRequestMappings());
		onStart(channel);
	}

	@Override
	public final void stop() {
		onStop();
	}
	
	protected abstract void onStart(T channel);
	protected abstract void onStop(); 

	@Override
	public void processRequest(String clientInfo,  Object request, OutputStream responseOutput) throws Exception {
		simulationRunner.runSimulation(responseOutput, channelName, clientInfo, requestMappings, request);
	}
}
