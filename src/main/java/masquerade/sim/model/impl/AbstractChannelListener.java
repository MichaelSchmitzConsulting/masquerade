package masquerade.sim.model.impl;

import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.ChannelListenerContext;
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
	private ChannelListenerContext context;
	
	@Override
	public final void start(T channel, SimulationRunner simulationRunner, ChannelListenerContext context) {
		this.simulationRunner = simulationRunner;
		this.channelName = channel.getName();
		this.requestMappings = new LinkedHashSet<RequestMapping<?>>(channel.getRequestMappings());
		this.context = context;
		onStart(channel);
		this.context = null;
	}

	@Override
	public final void stop(ChannelListenerContext context) {
		this.context = context;
		onStop();
		this.context = null;
		this.simulationRunner = null;
		this.channelName = null;
		this.requestMappings = null;
	}
	
	protected abstract void onStart(T channel);
	protected abstract void onStop(); 
	protected ChannelListenerContext getContext() {
		return context;
	}
	
	@Override
	public void processRequest(String clientInfo,  Object request, OutputStream responseOutput) throws Exception {
		simulationRunner.runSimulation(responseOutput, channelName, clientInfo, requestMappings, request);
	}
}
