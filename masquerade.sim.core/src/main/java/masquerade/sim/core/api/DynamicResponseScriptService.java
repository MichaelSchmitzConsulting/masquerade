package masquerade.sim.core.api;

import static masquerade.sim.core.api.ResponseTemplate.errorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;
import masquerade.sim.util.XStreamUnmarshallerFactory;
import masquerade.sim.util.XmlScriptUnmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for clients to setup response scripts when a specified request
 * ID is encountered.
 * 
 * @see ResponseProvider
 */
@Component(name="dynamicResponseScriptServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class DynamicResponseScriptService extends HttpServlet {
	private static final StatusLog log = StatusLogger.get(DynamicResponseScriptService.class);
	
	@Reference protected ResponseProvider responseProvider;
	@Reference protected PluginRegistry pluginRegistry;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestId = StringUtil.removeLeadingSlash(req.getPathInfo());
		
		if (StringUtils.isNotEmpty(requestId)) {
			provideDynamicResponseScript(requestId, req.getInputStream());
		} else {
			errorResponse(resp, "Missing simulation request ID in HTTP request URL");
		}
	}

	protected void provideDynamicResponseScript(String requestId, InputStream inputStream) throws IOException {
		List<SimulationStep> steps = unmarshalSteps(inputStream);
		responseProvider.provideResponseScript(requestId, steps);
		log.trace("Uploaded dynamic script with " + steps.size() + " steps for request ID " + requestId);
	}

	private List<SimulationStep> unmarshalSteps(InputStream inputStream) throws IOException {
		XStreamUnmarshallerFactory factory = new XStreamUnmarshallerFactory(pluginRegistry);
		XmlScriptUnmarshaller unmarshaller = new XmlScriptUnmarshaller(factory);
		String content = IOUtils.toString(inputStream);
		List<SimulationStep> steps = unmarshaller.unmarshal(content);
		return steps;
	}
}
