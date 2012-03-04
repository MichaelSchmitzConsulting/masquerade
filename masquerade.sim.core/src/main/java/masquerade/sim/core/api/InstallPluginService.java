package masquerade.sim.core.api;

import static masquerade.sim.core.api.ResponseTemplate.errorResponse;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.plugin.PluginException;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for installing plugins by uploading the bundle JAR
 */
@Component(name="installPluginServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class InstallPluginService extends HttpServlet {
	private static final StatusLog log = StatusLogger.get(InstallPluginService.class);
	
	@Reference PluginManager pluginManager;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pluginName = StringUtil.removeLeadingSlash(req.getPathInfo());
		
		if (isNotEmpty(pluginName) && !disallowed(pluginName)) {
			try {
				installPlugin(pluginName, req.getInputStream());
			} catch (PluginException e) {
				log.error("Remote plugin installation failed", e);
				errorResponse(resp, "Plugin installation failed: " + e.getMessage());
			}
		} else {
			errorResponse(resp, "Missing or invalid plugin name in HTTP request URL");
		}
	}

	protected void installPlugin(String pluginName, InputStream inputStream) throws PluginException {
		pluginManager.installPlugin(pluginName, inputStream);
	}

	private static boolean disallowed(String pluginName) {
		return pluginName.contains("..") || pluginName.contains("\\") || pluginName.contains("/");
	}
}
