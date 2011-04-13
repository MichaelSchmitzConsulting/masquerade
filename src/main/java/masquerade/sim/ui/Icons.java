package masquerade.sim.ui;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public enum Icons {
	CHANNELS("globe.png"),
	RESPONSE("email-send.png"),
	SCRIPT("document-txt.png"),
	REQUEST_MAPPING("arrow-right.png"),
	REQUEST_ID_PROVIDER("note.png"),
	REQUEST_HISTORY("document-txt.png");
	
	private static final String BASE_PATH = "../runo/icons/32/";
	
	private Resource resource;
	
	private Icons(String path) {
		this.resource = new ThemeResource(BASE_PATH + path);
	}
	
	public Resource icon() {
		return resource;
	}
}
