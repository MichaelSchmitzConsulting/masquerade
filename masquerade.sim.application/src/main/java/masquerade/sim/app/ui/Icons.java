package masquerade.sim.app.ui2;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;

public enum Icons {
	LISTENER("channel.png"),
	SIMULATION("script.png"),
	REQUEST_HISTORY("history.png"),
	STATUS("status.png"),
	ARTIFACT("artifact.png"),
	TEST("test.png"),
	SETTINGS("settings.png"),
	PLUGINS("plugins.png"),
	IMPORTEXPORT("importexport.png");
	
	private final String path;
	private volatile Resource resource;
	
	private Icons(String path) {
		this.path = path;
	}
	
	public Resource icon(String baseUrl) {
		if (resource == null) {
			String url = baseUrl + "/res/icon/" + path;
	    	resource = new ExternalResource(url);
		}
		return resource;
	}
}
