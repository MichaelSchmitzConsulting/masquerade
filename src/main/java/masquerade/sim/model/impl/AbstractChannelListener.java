package masquerade.sim.model.impl;

import java.io.OutputStream;
import java.util.Set;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.util.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class AbstractChannelListener<T extends Channel> implements ChannelListener<T> {
	private RequestHistoryFactory requestHistoryFactory;

	@Override
	public final void start(T channel, RequestHistoryFactory requestHistoryFactory) {
		this.requestHistoryFactory = requestHistoryFactory;
		onStart(channel);
	}

	@Override
	public final void stop() {
		onStop();
	}
	
	protected abstract void marshalResponse(Object response, OutputStream responseOutput);	
	protected abstract void onStart(T channel);
	protected abstract void onStop(); 

	// TODO: Refactor processing logic out of channel, store config in DB and implement processing logic in live object
	protected void processRequest(Channel channel, String clientInfo, 
			Object request, OutputStream responseOutput) throws Exception {
		
		Set<RequestMapping<?>> requestMappings = channel.getRequestMappings();
		String channelName = channel.getName();
		
		boolean simulationMatches = false;
		RequestHistory requestHistory = requestHistoryFactory.createRequestHistory();

		try {
			for (RequestMapping<?> mapping : requestMappings) {
				if (mapping.accepts(request.getClass()) && matches(mapping, request)) {
					ResponseSimulation simulation = mapping.getResponseSimulation();
					simulationMatches = true;
					
					String requestId = getRequestId(simulation.getRequestIdProvider(), request);
					requestHistory.logRequest(channelName, simulation.getName(), clientInfo, requestId, marshal(request));
					
					Object response = simulation.getScript().run(request);
					marshalResponse(response, responseOutput);
				}
			}
			
			if (!simulationMatches) {
				requestHistory.logRequest(channelName, null, clientInfo, null, marshal(request));
			}
		} finally {
			requestHistory.endSession();
		}
	}

	// TODO: Marshalling/unmarshalling
	private String marshal(Object request) {
		String str;
		
		if (request instanceof Document) {
			str = DomUtil.asString((Node) request);
		} else {
			str = request.toString();
		}
		
		return str;
	}

	// Casts are safe because accepts() checks the request type
	private boolean matches(RequestMapping<?> mapping, Object request) {
		@SuppressWarnings("rawtypes")
		RequestMapping cast = (RequestMapping) mapping;
		@SuppressWarnings("unchecked")
		boolean matches = cast.matches(request);
		return matches;
	}
 
	// Casts are safe because ResponseSimulation.matches() is typed
	private String getRequestId(RequestIdProvider<?> requestIdProvider, Object request) {
		if (requestIdProvider == null) {
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		RequestIdProvider cast = requestIdProvider;
		@SuppressWarnings("unchecked")
		String uniqueId = cast.getUniqueId(request);
		return uniqueId;
	}
}
