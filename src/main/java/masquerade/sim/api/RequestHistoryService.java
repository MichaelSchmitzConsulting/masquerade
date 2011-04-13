package masquerade.sim.api;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.ApplicationContext;
import masquerade.sim.ApplicationLifecycle;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.IOUtils;

import static org.apache.commons.lang.StringUtils.*;

/**
 * Returns request data for the specified request ID, or HTTP status 404 if not found
 * 
 */
public class RequestHistoryService extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ApplicationContext applicationContext = ApplicationLifecycle.getApplicationContext(getServletContext());
		RequestHistory requestHistory = applicationContext.getRequestHistoryFactory().startRequestHistorySession();
		
		try {
			streamRequestContent(req, resp, requestHistory);
		} finally {
			requestHistory.endSession();
		}
	}

	private void streamRequestContent(HttpServletRequest req, HttpServletResponse resp, RequestHistory requestHistory) throws IOException {
		String requestId = StringUtil.removeLeadingSlash(req.getPathInfo());
		InputStream content;
		if (isNotEmpty(requestId) && (content = requestHistory.getRequest(requestId)) != null) {
			ServletOutputStream out = resp.getOutputStream();
			IOUtils.copy(content, out);
			out.close();
		} else {
			if (isEmpty(requestId)) {
				requestId = "<missing>";
			}
			resp.getOutputStream().print("Request " + requestId + " not found");
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
