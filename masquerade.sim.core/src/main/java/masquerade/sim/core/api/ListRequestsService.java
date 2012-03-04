package masquerade.sim.core.api;

import static masquerade.sim.core.api.RequestHistoryService.DATE_FORMAT_PATTERN;
import static masquerade.sim.core.api.RequestHistoryService.HEADER_CHANNEL_NAME;
import static masquerade.sim.core.api.RequestHistoryService.HEADER_CLIENT_INFO;
import static masquerade.sim.core.api.RequestHistoryService.HEADER_PROCESSING_PERIOD;
import static masquerade.sim.core.api.RequestHistoryService.HEADER_RECEIVE_TIME;
import static masquerade.sim.core.api.RequestHistoryService.HEADER_REQUEST_TIME;
import static masquerade.sim.core.api.RequestHistoryService.HEADER_REQUEST_TIMESTAMP;
import static masquerade.sim.core.api.ResponseTemplate.errorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * API Service returning a list of requests matching a certain request
 * ID pattern
 * 
 * @see Pattern
 */
@Component(name="listRequestsServlet")
@Service(Servlet.class)
@SuppressWarnings("serial")
public class ListRequestsService extends HttpServlet {
	static final String HEADER_REQUEST_ID = "X-Masquerade-RequestId";

	private ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override protected DateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT_PATTERN);
		};
	};

	@Reference protected RequestHistory requestHistory;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestIdPattern = StringUtil.removeLeadingSlash(req.getPathInfo());
		
		if (StringUtils.isNotEmpty(requestIdPattern)) {
			listRequests(requestIdPattern, resp.getOutputStream());
		} else {
			errorResponse(resp, "Missing simulation request ID pattern in HTTP request URL");
		}
	}

	protected void listRequests(String requestIdPrefix, OutputStream outputStream) throws IOException {
		List<HistoryEntry> requests = requestHistory.getRequestsForIdPrefix(requestIdPrefix);
		
		marshalRequests(requests, outputStream);
	}

	private void marshalRequests(List<HistoryEntry> requests, OutputStream outputStream) throws IOException {
		// Multipart cannot be empty - leave empty response for empty lists
		if (requests.isEmpty()) 
			return;
		
		Multipart multipart = new MimeMultipart();
		try {
			for (HistoryEntry entry : requests) {
				InputStream data = entry.readRequestData();
				MimeBodyPart bodyPart = new MimeBodyPart();
				String text = IOUtils.toString(data);
				bodyPart.setText(text);
				setHeaders(entry, bodyPart);
				multipart.addBodyPart(bodyPart);
			}
			multipart.writeTo(outputStream);
		} catch (MessagingException e) {
			throw new IOException("unable to wrap request data into mime multipart", e);
		}
	}

	private void setHeaders(HistoryEntry entry, MimeBodyPart bodyPart) throws MessagingException {
		bodyPart.addHeader(HEADER_REQUEST_ID, entry.getRequestId());

		bodyPart.addHeader(HEADER_REQUEST_TIMESTAMP, DATE_FORMAT.get().format(entry.getRequestTimestamp()));
		bodyPart.addHeader(HEADER_REQUEST_TIME, String.valueOf(entry.getRequestTime()));
		bodyPart.addHeader(HEADER_RECEIVE_TIME, String.valueOf(entry.getReceiveTime()));
		bodyPart.addHeader(HEADER_PROCESSING_PERIOD, String.valueOf(entry.getProcessingPeriod()));
		bodyPart.addHeader(HEADER_CHANNEL_NAME, entry.getChannelName());
		bodyPart.addHeader(HEADER_CLIENT_INFO, entry.getClientInfo());
	}
}
