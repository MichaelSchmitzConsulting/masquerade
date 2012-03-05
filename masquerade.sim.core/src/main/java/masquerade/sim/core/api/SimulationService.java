package masquerade.sim.core.api;

import static masquerade.sim.core.api.RequestTemplate.pathMatches;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API service providing access to simulations in the repository (insert/delete)
 */
@Component(name="simulationServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class SimulationService extends HttpServlet {
	private static final String PARAM_CHANNEL_ID = "channelId";
	private static final String ID = "/id";
	private static final Pattern SIMULATION_ID_PATTERN = Pattern.compile(ID + "/(.*)");
	private static final String ALL = "/all";

	@Reference ModelRepository modelRepository;
	@Reference Importer importer;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (ID.equals(req.getPathInfo())) {
			SimulationTemplate simulationTemplate = new SimulationTemplate(modelRepository, importer);
			simulationTemplate.insertSimulation(req.getInputStream(), req.getParameterValues(PARAM_CHANNEL_ID));
		} else {
			ResponseTemplate.errorResponse(resp, "Invalid request URL");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id;
		if ((id = pathMatches(req.getPathInfo(), SIMULATION_ID_PATTERN)) != null) {
			modelRepository.deleteSimulation(id);
		} else if (ALL.equals(req.getPathInfo())) {
			modelRepository.deleteSimulations();
		} else {
			ResponseTemplate.errorResponse(resp, "Invalid request URL");
		}
	}
}
