package masquerade.sim.app;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpContext;

/**
 * A {@link HttpContext} serving resources from the classpath
 */
public class ResourceHttpContext implements HttpContext {
	@Override
	public boolean handleSecurity(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException {
		return true;
	}

	@Override
	public URL getResource(String name) {
		if (!name.startsWith ("/")) {
			name = "/" + name;
		}
		
		return getClass().getResource(name);
	}

	@Override
	public String getMimeType(String unused) {
		return null;
	}
}