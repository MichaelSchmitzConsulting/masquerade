package masquerade.sim.model;

import java.util.Map;

/**
 * Resolves namespaces by prefix, and is able to provide a list of known
 * namespaces.
 */
public interface NamespaceResolver {
	/**
	 * @return Namespace for this prefix, or <code>null</code> if not found
	 */
	String resolveNamespacePrefix(String prefix);
	
	/**
	 * @return All known namespace prefixes
	 */
	Map<String, String> getKnownNamespaces();
}
