package masquerade.sim.app;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * ServletConfig adapter forwarding calls to another {@link ServletConfig}
 * instance, except for init paramters which are provided by this adapter
 */
public class ServletConfigAdapter implements ServletConfig {

	private final ServletConfig delegate;
	private final Map<String, String> initParameters;

	public ServletConfigAdapter(ServletConfig delegate, Map<String, String> initParameters) {
		this.delegate = delegate;
		this.initParameters = new LinkedHashMap<String, String>(initParameters);
	}

	@Override
	public String getServletName() {
		return delegate.getServletName();
	}

	@Override
	public ServletContext getServletContext() {
		return delegate.getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		return initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return new Hashtable<String, String>(initParameters).keys();
	}	
}
