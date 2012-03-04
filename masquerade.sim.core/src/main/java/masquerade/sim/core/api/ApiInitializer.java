package masquerade.sim.core.api;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

/**
 * Register the REST api servlets using OSGi's {@link HttpService}
 */
@Component(immediate=true)
public class ApiInitializer {

	private static final String API_REQUESTHISTORY_PATH = "/api/requestHistory";
	private static final String API_PROVIDERESPONSE_PATH = "/api/provideResponse";
	private static final String API_LISTREQUESTS_PATH = "/api/listRequests";
	private static final String API_PLUGIN_INSTALL_PATH = "/api/plugin/install";
	private static final String API_PLUGIN_UNINSTALL_PATH = "/api/plugin/uninstall";
	private static final String API_SETTINGS_PROPERTIES_PATH = "/api/settings/configurationProperties";
	private static final String API_DYNAMIC_RESPONSE_SCRIPT_PATH = "/api/dynamicResponseScript";
	private static final String API_REMOVE_RESPONSE_SCRIPTS_PATH = "/api/removeResponseScripts";
	
	private static final StatusLog log = StatusLogger.get(ApiInitializer.class);
	
	@Reference protected HttpService httpService;
	@Reference(target="(component.name=requestHistoryServlet)") protected Servlet requestHistoryServlet;
	@Reference(target="(component.name=provideResponseServlet)") protected Servlet provideResponseServlet;
	@Reference(target="(component.name=listRequestsServlet)") protected Servlet listRequestsServlet;
	@Reference(target="(component.name=installPluginServlet)") protected Servlet installPluginServlet;
	@Reference(target="(component.name=uninstallPluginServlet)") protected Servlet uninstallPluginServlet;
	@Reference(target="(component.name=configurationPropertiesServlet)") protected Servlet configurationPropertiesServlet;
	@Reference(target="(component.name=dynamicResponseScriptServlet)") protected Servlet dynamicResponseScriptServlet;
	@Reference(target="(component.name=removeResponseScriptsServlet)") protected Servlet removeResponseScriptsServlet;
	
	@Activate
	protected void activate(ComponentContext componentContext) throws ServletException, NamespaceException {		
		// Register request history service servlet
		register(API_REQUESTHISTORY_PATH, requestHistoryServlet);

		// Register response provider service servlet
		register(API_PROVIDERESPONSE_PATH, provideResponseServlet);
		
		// Register response provider service servlet
		register(API_LISTREQUESTS_PATH, listRequestsServlet);

		// Register un/-install plugin service servlet
		register(API_PLUGIN_INSTALL_PATH, installPluginServlet);
		register(API_PLUGIN_UNINSTALL_PATH, uninstallPluginServlet);

		// Register configuration properties service servlet
		register(API_SETTINGS_PROPERTIES_PATH, configurationPropertiesServlet);
		
		// Register dynamic response servlets
		register(API_DYNAMIC_RESPONSE_SCRIPT_PATH, dynamicResponseScriptServlet);
		register(API_REMOVE_RESPONSE_SCRIPTS_PATH, removeResponseScriptsServlet);
		
		log.info("HTTP API URLs registered");
	}
	
	private void register(String path, Servlet servlet) throws ServletException, NamespaceException {
		httpService.registerServlet(path, servlet, null, null);
	}
}
