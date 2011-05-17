package masquerade.sim.api;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.ApplicationContext;
import masquerade.sim.ApplicationLifecycle;
import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.IOUtils;

/**
 * Returns request data for the specified request ID, or HTTP status 404 if not found.
 *<p>
 * Sets a header <code>X-Masquerade-RequestTimestamp</code> with the timestmap the 
 * request was originally received. The timestamp is formatted as 
 * <code>yyyy-MM-ddTHH:mm:ss:SSS</code>.
 */
public class RequestHistoryService extends HttpServlet {
	
	private static final String HEADER_REQUEST_TIMESTAMP = "X-Masquerade-RequestTimestamp";
	private static final String HEADER_REQUEST_TIME = "X-Masquerade-RequestTime";
	private static final String HEADER_CHANNEL_NAME = "X-Masquerade-ChannelName";
	private static final String HEADER_CLIENT_INFO = "X-Masquerade-ClientInfo";
	
	private ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		};
	};

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
		HistoryEntry entry;
		if (isNotEmpty(requestId) && (entry = requestHistory.getRequest(requestId)) != null) {
			setResponseHeaders(resp, entry);
			ServletOutputStream out = resp.getOutputStream();
			IOUtils.copy(entry.readRequestData(), out);
			out.close();
		} else {
			if (isEmpty(requestId)) {
				requestId = "<missing>";
			}
			resp.getOutputStream().print("Request " + requestId + " not found");
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void setResponseHeaders(HttpServletResponse resp, HistoryEntry entry) {
		resp.setHeader(HEADER_REQUEST_TIMESTAMP, DATE_FORMAT.get().format(entry.getTimestamp()));
		resp.setHeader(HEADER_REQUEST_TIME, String.valueOf(entry.getTimestamp().getTime()));
		resp.setHeader(HEADER_CHANNEL_NAME, entry.getChannelName());
		resp.setHeader(HEADER_CLIENT_INFO, entry.getClientInfo());
	}
}
