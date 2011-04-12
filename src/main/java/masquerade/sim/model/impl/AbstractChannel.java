package masquerade.sim.model.impl;

import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.util.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * {@link Channel} implementation defining common properties
 */
public abstract class AbstractChannel implements Channel {

	private String name;
	private Set<RequestMapping<?>> requestMappings = new LinkedHashSet<RequestMapping<?>>();
	private transient RequestHistoryFactory requestHistoryFactory;

	protected AbstractChannel(String name) {
		this.name = name;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return toString();
	}

	@Override
	public final Set<RequestMapping<?>> getRequestMappings() {
		return new LinkedHashSet<RequestMapping<?>>(requestMappings);
	}

	@Override
	public void setRequestMappings(Set<RequestMapping<?>> requestMappings) {
		if (requestMappings == null) {
			this.requestMappings = new LinkedHashSet<RequestMapping<?>>();
		} else {
			this.requestMappings = new LinkedHashSet<RequestMapping<?>>(requestMappings);
		}
	}

	@Override
	public final void start(RequestHistoryFactory requestHistoryFactory) {
		this.requestHistoryFactory = requestHistoryFactory;
		onStart();
	}

	@Override
	public final void stop() {
		onStop();
	}

	protected void onStart() { }
	protected void onStop() { } 

	protected abstract void marshalResponse(Object response, OutputStream responseOutput);
	
	// TODO: Refactor processing logic out of channel, store config in DB and implement processing logic in live object
	protected void processRequest(String clientInfo, Object request, OutputStream responseOutput) throws Exception {
		boolean simulationMatches = false;
		RequestHistory requestHistory = requestHistoryFactory.getRequestHistory();

		try {
			for (RequestMapping<?> mapping : requestMappings) {
				if (mapping.accepts(request.getClass()) && matches(mapping, request)) {
					ResponseSimulation simulation = mapping.getResponseSimulation();
					simulationMatches = true;
					
					String requestId = getRequestId(simulation.getRequestIdProvider(), request);
					requestHistory.logRequest(getName(), simulation.getName(), clientInfo, requestId, marshal(request));
					
					Object response = simulation.getScript().run(request);
					marshalResponse(response, responseOutput);
				}
			}
			
			if (!simulationMatches) {
				requestHistory.logRequest(getName(), null, clientInfo, null, marshal(request));
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
