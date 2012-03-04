package masquerade.sim.core.api;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.Settings;
import masquerade.sim.model.repository.ModelRepository;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for setting configuration properties
 */
@Component(name="configurationPropertiesServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class ConfigurationPropertiesService extends HttpServlet {
	@Reference ModelRepository modelRepository;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String props = IOUtils.toString(req.getInputStream());

		setConfigurationProperties(props);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String props = getConfigurationProperties();
		ServletOutputStream response = resp.getOutputStream();
		response.print(props);
		response.flush();
	}

	protected String getConfigurationProperties() {
		String props  = modelRepository.getSettings().getConfigurationProperties();
		return props == null ? "" : props;
	}

	protected void setConfigurationProperties(String props) {
		Settings settings = modelRepository.getSettings();
		settings.setConfigurationProperties(props);
		modelRepository.updateSettings(settings);
	}
}
