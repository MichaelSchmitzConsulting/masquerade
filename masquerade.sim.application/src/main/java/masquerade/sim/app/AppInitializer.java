package masquerade.sim.app;

import static masquerade.sim.app.UrlConstants.APP_PATH;
import static masquerade.sim.app.UrlConstants.RESOURCE_PATH;
import static masquerade.sim.app.UrlConstants.VAADIN_RESOURCE_PATH;

import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.vaadin.terminal.gwt.server.Constants;

/**
 * Register the Vaadin application servlet and the embedded HTTP 
 * channel servlet using the OSGi HttpService.
 */
@Component
public class AppInitializer {

	private static final String ROOT = "/";
	private static final String WIDGET_SET = "masquerade.sim.app.ui.widgetset.WidgetSet";
	
	private static final StatusLog log = StatusLogger.get(AppInitializer.class);
	
	@Reference protected HttpService httpService;
	@Reference(target="(component.name=appServlet)") protected Servlet appServlet; 
	@Reference(target="(component.name=redirectServlet)") protected Servlet redirectServlet; 
	
	/** OSGi component startup callback */
	@Activate
	protected void activate(ComponentContext componentContext) throws ServletException, NamespaceException {
		registerApplicationServlet(httpService);
		registerVaadinResources(httpService);
		registerResource(RESOURCE_PATH, "/masquerade/sim/app/res", httpService);
		
		httpService.registerServlet(ROOT, redirectServlet, null, null);
		
		log.info("Masquerade App started");
	}
	
	/** OSGi component shutdown callback */
	@Deactivate
	protected void deactivate(ComponentContext componentContext) {
		httpService.unregister(APP_PATH);
		httpService.unregister(VAADIN_RESOURCE_PATH);
		httpService.unregister(RESOURCE_PATH);
		httpService.unregister(ROOT);
	}

	private void registerVaadinResources(HttpService httpService) throws NamespaceException {
		registerResource(VAADIN_RESOURCE_PATH, VAADIN_RESOURCE_PATH, httpService);
	}

	private void registerResource(String path, String location, HttpService httpService) throws NamespaceException {
		httpService.registerResources(path, location, new ResourceHttpContext());
	}
	
	private void registerApplicationServlet(HttpService httpService) throws ServletException, NamespaceException {
		Properties initParams = new Properties();
		initParams.setProperty("application", MasqueradeApplication.class.getName());
		initParams.setProperty("widgetset", WIDGET_SET);
		initParams.setProperty(Constants.SERVLET_PARAMETER_PRODUCTION_MODE, Boolean.TRUE.toString());

		HttpServlet vaadinServlet = (HttpServlet) appServlet;
		httpService.registerServlet(APP_PATH, vaadinServlet, initParams, null);
	}
}