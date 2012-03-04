package masquerade.sim.core.api;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * 
 */
@Component(name="simulationServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class SimulationService extends HttpServlet {

}
