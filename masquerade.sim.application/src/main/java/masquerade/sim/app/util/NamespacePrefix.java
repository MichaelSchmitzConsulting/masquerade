package masquerade.sim.app.util;

public class NamespacePrefix {
	public static final String PROP_PREFIX = "prefix";
	public static final String PROP_URI = "uri";
	
	private String prefix;
	private String uri;
	public NamespacePrefix(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
}