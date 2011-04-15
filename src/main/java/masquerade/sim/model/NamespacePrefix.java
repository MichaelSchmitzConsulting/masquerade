package masquerade.sim.model;

public class NamespacePrefix {
	private String prefix = "ns";
	private String namespace = "http://example.com/ns";
	
	/**
	 * @param prefix
	 * @param namespace
	 */
	public NamespacePrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getName() {
		return prefix;
	}
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
		return "Namespace Prefix: " + prefix;
	}
}
