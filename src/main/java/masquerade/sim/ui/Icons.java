package masquerade.sim.ui;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;

public enum Icons {
	CHANNELS("channel.png"),
	SCRIPT("script.png"),
	REQUEST_MAPPING("requestmapping.png"),
	REQUEST_ID_PROVIDER("idprovider.png"),
	REQUEST_HISTORY("history.png"),
	STATUS("status.png"),
	ARTIFACT("artifact.png"),
	TEST("test.png"),
	NAMESPACE_PREFIX("namespace.png"),
	SETTINGS("settings.png");
	
	private final String path;
	private volatile Resource resource;
	
	private Icons(String path) {
		this.path = path;
	}
	
	public Resource icon(String baseUrl) {
		if (resource == null) {
			String url = baseUrl + "/icon/" + path;
	    	resource = new ExternalResource(url);
		}
		return resource;
	}
}
