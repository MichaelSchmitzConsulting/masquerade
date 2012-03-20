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
	
	void setPrefixes(Map<String, String> pfxs);
	
	void addPrefix(String prefix, String URI);
	void addPrefixes(Map<String, String> prefixes);
	
	void removePrefix(String prefix);

}
