package masquerade.sim.core.api;

import static masquerade.sim.core.api.ResponseTemplate.errorResponse;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service for clients to setup responses when a specified request
 * ID is encountered.
 * 
 * @see ResponseProvider
 */
@Component(name="provideResponseServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class ProvideResponseService extends HttpServlet {
	@Reference protected ResponseProvider responseProvider;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestId = StringUtil.removeLeadingSlash(req.getPathInfo());
		
		if (StringUtils.isNotEmpty(requestId)) {
			provideRequest(requestId, req.getInputStream());
		} else {
			errorResponse(resp, "Missing simulation request ID in HTTP request URL");
		}
	}

	private void provideRequest(String requestId, InputStream inputStream) throws IOException {
		byte[] response = IOUtils.toByteArray(inputStream);
		responseProvider.provideResponse(requestId, response);
	}
}
