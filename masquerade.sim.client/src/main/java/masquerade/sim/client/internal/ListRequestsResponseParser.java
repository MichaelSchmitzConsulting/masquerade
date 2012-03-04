package masquerade.sim.client.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import masquerade.sim.client.MasqueradeClientException;
import masquerade.sim.client.Request;

import org.apache.commons.io.IOUtils;

/**
 * Parses a MIME multipart response from a listRequests HTTP API request
 */
public class ListRequestsResponseParser {
	private static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";
	
	public static List<Request> parse(InputStream responseContent) {
		try {
			return doParse(responseContent);
		} catch (MessagingException e) {
			throw new MasqueradeClientException("Malformed MIME multipart response", e);
		} catch (IOException e) {
			throw new MasqueradeClientException("I/O exception while reading stream", e);
		}
	}

	private static List<Request> doParse(InputStream responseContent) throws MessagingException, IOException {
		byte[] arr = IOUtils.toByteArray(responseContent);
		if (arr.length == 0) {
			return Collections.emptyList();
		}
		
		MimeMultipart multipart = createMultipart(arr);
		
		int count = multipart.getCount();
		List<Request> requests = new ArrayList<Request>(count); 
		for (int i = 0; i < count; i++) {
		  BodyPart part = multipart.getBodyPart(i);
		  String content = (String) part.getContent();
		  String requestId = part.getHeader("X-Masquerade-RequestId")[0];
		  Request request = new RequestImpl(requestId, content);
		  requests.add(request);
		}
		return requests;
	}

	private static MimeMultipart createMultipart(byte[] arr) throws MessagingException {
		ByteArrayDataSource dataSource = new ByteArrayDataSource(arr, MULTIPART_CONTENT_TYPE);
		MimeMultipart multipart = 
		  new MimeMultipart(
		    dataSource);
		return multipart;
	}
}
