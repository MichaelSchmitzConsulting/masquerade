package masquerade.sim.channel.http.impl;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

@Component
public class HttpChannelInitializer {

	private static final String REQUEST_PATH = "/request";
	
	@Reference protected HttpService httpService;
	@Reference(target="(component.name=httpChannelServlet)") protected Servlet httpChannelServlet; 

	/** OSGi component startup callback */
	@Activate
	protected void activate(ComponentContext componentContext) throws ServletException, NamespaceException {
		httpService.registerServlet(REQUEST_PATH, httpChannelServlet, null, null);
	}
	
	/** OSGi component shutdown callback */
	@Deactivate
	protected void deactivate(ComponentContext componentContext) {
		httpService.unregister(REQUEST_PATH);
	}
	
}
