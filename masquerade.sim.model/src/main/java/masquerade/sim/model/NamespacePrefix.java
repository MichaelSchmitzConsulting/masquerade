package masquerade.sim.model;

public class NamespacePrefix implements Named {
	private String name = "ns";
	private String namespace = "http://example.com/ns";
	
	/**
	 * @param prefix
	 * @param namespace
	 */
	public NamespacePrefix(String prefix) {
		this.name = prefix;
	}

	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return name;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.name = prefix;
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
		return "Namespace Prefix: " + name;
	}
}
