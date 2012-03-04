package masquerade.sim.core.api;

import static masquerade.sim.core.api.ResponseTemplate.errorResponse;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for clients to remove previously uploaded response scripts
 * 
 * @see ResponseProvider
 */
@Component(name="removeResponseScriptsServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class RemoveResponseScriptsService extends HttpServlet {
	private static final StatusLog log = StatusLogger.get(RemoveResponseScriptsService.class);
	
	@Reference protected ResponseProvider responseProvider;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestIdPrefix = StringUtil.removeLeadingSlash(req.getPathInfo());
		
		if (StringUtils.isNotEmpty(requestIdPrefix)) {
			int removeCount = removeResponseScripts(requestIdPrefix);
			writeRemoveCountResponse(resp.getOutputStream(), removeCount, requestIdPrefix);
		} else {
			errorResponse(resp, "Missing simulation request prefix in HTTP request URL");
		}
	}

	private int removeResponseScripts(String requestIdPrefix) {
		return responseProvider.removeResponseScripts(requestIdPrefix);
	}

	private void writeRemoveCountResponse(ServletOutputStream outputStream, int removeCount, String requestIdPrefix) throws IOException {
		String msg = "Removed " + removeCount + " scripts for request ID prefix " + requestIdPrefix;
		outputStream.println(msg);
		log.trace(msg);
	}
}
