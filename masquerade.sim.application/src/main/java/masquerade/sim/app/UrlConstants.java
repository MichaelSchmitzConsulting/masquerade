package masquerade.sim.app;

/**
 * URL paths for application servlets and static content
 */
public final class UrlConstants {
	public static final String APP_PATH = "/app";
	public static final String APP_REDIRECT = APP_PATH.substring(1) + "/";
	
	public static final String RESOURCE_PATH = "/res";
	public static final String VAADIN_RESOURCE_PATH = "/VAADIN";

	private UrlConstants() { }
}
