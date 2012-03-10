package masquerade.sim.model;

public class NamespacePrefix {
	private String pfx = "ns";
	private String namespace = "http://example.com/ns";
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return pfx;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.pfx = prefix;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getDescription() {
		return namespace;
	}
	
	@Override
	public String toString() {
		return "Namespace Prefix: " + pfx;
	}
}
