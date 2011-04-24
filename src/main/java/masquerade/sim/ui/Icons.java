package masquerade.sim.ui;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public enum Icons {
	CHANNELS("globe.png"),
	RESPONSE("email-send.png"),
	SCRIPT("document-txt.png"),
	REQUEST_MAPPING("arrow-right.png"),
	REQUEST_ID_PROVIDER("note.png"),
	REQUEST_HISTORY("email-send.png"),
	STATUS("email-send.png"),
	ARTIFACT("document-txt.png"),
	TEST("help.png"),
	NAMESPACE_PREFIX("ok.png");
	
	private static final String BASE_PATH = "../runo/icons/";
	private static final String _32 = "32/";
	private static final String _16 = "16/";
	
	private final Resource resource16;
	private final Resource resource32;
	
	private Icons(String path) {
		this.resource16 = new ThemeResource(BASE_PATH + _16 + path);
		this.resource32 = new ThemeResource(BASE_PATH + _32 + path);
	}
	
	public Resource icon() {
		return resource32;
	}

	public Resource icon16() {
		return resource16;
	}
}
