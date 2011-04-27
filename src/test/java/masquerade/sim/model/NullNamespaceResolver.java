package masquerade.sim.model;

import java.util.Collections;
import java.util.Map;

/**
 * Stub implementation of {@link NamespaceResolver}
 */
public class NullNamespaceResolver implements NamespaceResolver {

	@Override
	public String resolveNamespacePrefix(String prefix) {
		return null;
	}

	@Override
	public Map<String, String> getKnownNamespaces() {
		return Collections.emptyMap();
	}

}
