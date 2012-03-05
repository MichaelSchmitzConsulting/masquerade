package masquerade.sim.model.impl;

import java.util.Map;

import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Simulation;

/**
 * Default implementation of {@link Simulation}, defines how a response to a request is simulated. 
 */
public class SimulationImpl implements Simulation {

	private final String id;
	private final NamespaceResolverImpl namespaceResolver = new NamespaceResolverImpl();
	private RequestMapping<?> selector;
	private RequestIdProvider<?> requestIdProvider;
	private Script script;

	public SimulationImpl(String id, RequestMapping<?> selector, RequestIdProvider<?> requestIdProvider, Script script) {
		this.id = id;
		this.selector = selector;
		this.requestIdProvider = requestIdProvider;
		this.script = script;
	}

	/** Constructor for serialization */
	protected SimulationImpl(String id) {
		this.id = id;
	}

	public Map<String, String> getNamespacePrefixes() {
		return namespaceResolver.getKnownNamespaces();
	}
	
	public void setNamespacePrefixes(Map<String, String> pfxToNs) {
		for (Map.Entry<String, String> mapping : pfxToNs.entrySet()) {
			String prefix = mapping.getKey();
			String uri = mapping.getValue();
			namespaceResolver.declareNamespace(prefix, uri);
		}
	}
		
	@Override
	public String getId() {
		return id;
	}

	@Override
	public RequestMapping<?> getSelector() {
		return selector;
	}

	@Override
	public RequestIdProvider<?> getRequestIdProvider() {
		return requestIdProvider;
	}

	@Override
	public Script getScript() {
		return script;
	}

	@Override
	public NamespaceResolver getNamespaceResolver() {
		return namespaceResolver;
	}

	public void setSelector(RequestMapping<?> selector) {
		this.selector = selector;
	}

	public void setRequestIdProvider(RequestIdProvider<?> requestIdProvider) {
		this.requestIdProvider = requestIdProvider;
	}

	public void setScript(Script script) {
		this.script = script;
	}
}
