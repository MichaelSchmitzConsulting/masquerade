package masquerade.sim.core.api;

import static masquerade.sim.core.api.ResponseTemplate.errorResponse;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.plugin.Plugin;
import masquerade.sim.plugin.PluginException;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for uninstalling plugin bundles
 */
@Component(name="uninstallPluginServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class UninstallPluginService extends HttpServlet {
	private static final String SEPARATOR = ":";
	private static final StatusLog log = StatusLogger.get(UninstallPluginService.class);
	
	@Reference PluginManager pluginManager;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String nameAndVersion = StringUtil.removeLeadingSlash(req.getPathInfo());		
		
		if (StringUtils.isNotEmpty(nameAndVersion)) {
			try {
				uninstallPlugin(nameAndVersion, resp);
			} catch (PluginException e) {
				log.error("UninstallPluginService: Failed to uninstall plugin " + nameAndVersion + "upon API request", e);
				throw new ServletException("Exception while uninstalling plugin " + nameAndVersion, e);
			}
		} else {
			errorResponse(resp, "Missing plugin name in HTTP request URL");
		}
	}

	protected void uninstallPlugin(String nameAndVersion, HttpServletResponse resp) throws IOException, PluginException {
		String[] tuple = nameAndVersion.split(SEPARATOR);
		if (tuple.length == 2) {
			String name = tuple[0];
			String version = tuple[1];
			
			Plugin plugin = pluginManager.getPlugin(name, version);
			// Succeed anyway if plugin is not found - uninstalling a not yet installed plugin isn't an error
			if (plugin != null) {
				plugin.remove();
			}
		} else {
			ResponseTemplate.errorResponse(resp, "Invalid name:version string: " + nameAndVersion);
		}
	}
}
