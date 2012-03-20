package masquerade.sim.model.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import masquerade.sim.model.NamespaceResolver;

/**
 * Stub implementation of {@link NamespaceResolver} for use in tests
 */
public class NamespaceResolverImpl implements NamespaceResolver {
	private volatile Map<String, String> ns = new ConcurrentHashMap<String, String>();
	
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

	@Override
	public void addPrefix(String prefix, String URI) {
		ns.put(prefix, URI);
	}

	@Override
	public void addPrefixes(Map<String, String> prefixes) {
		for (Map.Entry<String, String> mapping : prefixes.entrySet()) {
			addPrefix(mapping.getKey(), mapping.getValue());
		}
	}

	@Override
	public void removePrefix(String prefix) {
		ns.remove(prefix);
	}

	@Override
	public void setPrefixes(Map<String, String> pfxs) {
		ns = new ConcurrentHashMap<String, String>(pfxs);
	}
}
