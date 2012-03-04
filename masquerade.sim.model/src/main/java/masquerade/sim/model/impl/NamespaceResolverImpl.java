package masquerade.sim.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import masquerade.sim.model.NamespaceResolver;

/**
 * Stub implementation of {@link NamespaceResolver} for use in tests
 */
public class NamespaceResolverImpl implements NamespaceResolver {
	private final Map<String, String> ns = new ConcurrentHashMap<String, String>();
	
	public void declareNamespace(String prefix, String uri) {
		ns.put(prefix, uri);
	}
	
	@Override
	public String resolveNamespacePrefix(String prefix) {
		return ns.get(prefix);
	}

	@Override
	public Map<String, String> getKnownNamespaces() {
		return new HashMap<String, String>(ns);
	}
}
