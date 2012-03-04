package masquerade.sim.app;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

/**
 * Vaadin's {@link ApplicationServlet} requires the application
 * class to be loadable using the classloader used for the ApplicationServlet.
 * This is something that doesn't work with OSGi if the ApplicationServlet
 * instance is loaded from the Vaadin bundle. This servlet
 * is thus a subclass of {@link ApplicationServlet} living 
 * in the same bundle as the application class.
 */
@Component(name="appServlet")
@Service(Servlet.class)
public class MasqueradeApplicationServlet extends ApplicationServlet {

	private static final long serialVersionUID = 1L;

	@Reference protected AppServiceLocator appServiceLocator;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {		
		servletConfig.getServletContext().setAttribute(AppServiceLocator.PROP_SERVICE_LOCATOR, appServiceLocator);
		super.init(servletConfig);
	}
}
