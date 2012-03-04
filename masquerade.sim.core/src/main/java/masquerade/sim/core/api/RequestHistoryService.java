package masquerade.sim.core.api;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * Returns request data for the specified request ID, or HTTP status 404 if not found.
 *<p>
 * Sets a header <code>X-Masquerade-RequestTimestamp</code> with the timestmap the 
 * request was originally received. The timestamp is formatted as 
 * <code>yyyy-MM-ddTHH:mm:ss:SSS</code>.
 */
@Component(name="requestHistoryServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class RequestHistoryService extends HttpServlet {
	static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	static final String HEADER_REQUEST_TIMESTAMP = "X-Masquerade-RequestTimestamp";
	static final String HEADER_REQUEST_TIME = "X-Masquerade-RequestTime";
	static final String HEADER_RECEIVE_TIME = "X-Masquerade-ReceiveTime";
	static final String HEADER_PROCESSING_PERIOD = "X-Masquerade-ProcessingPeriod";
	static final String HEADER_CHANNEL_NAME = "X-Masquerade-ChannelName";
	static final String HEADER_CLIENT_INFO = "X-Masquerade-ClientInfo";
	
	private ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override protected DateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT_PATTERN);
		};
	};

	@Reference protected RequestHistory requestHistory;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		streamRequestContent(req, resp, requestHistory);
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
		resp.setHeader(HEADER_REQUEST_TIMESTAMP, DATE_FORMAT.get().format(entry.getRequestTimestamp()));
		resp.setHeader(HEADER_REQUEST_TIME, String.valueOf(entry.getRequestTime()));
		resp.setHeader(HEADER_RECEIVE_TIME, String.valueOf(entry.getReceiveTime()));
		resp.setHeader(HEADER_PROCESSING_PERIOD, String.valueOf(entry.getProcessingPeriod()));
		resp.setHeader(HEADER_CHANNEL_NAME, entry.getChannelName());
		resp.setHeader(HEADER_CLIENT_INFO, entry.getClientInfo());
	}
}
