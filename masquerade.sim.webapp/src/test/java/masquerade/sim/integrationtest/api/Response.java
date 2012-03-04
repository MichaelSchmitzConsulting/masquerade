package masquerade.sim.integrationtest.api;

import masquerade.sim.util.DomUtil;

import org.w3c.dom.Document;

public class Response {
	
	private final Document content;
	private final String requestId;

	public Response(Document content, String requestId) {
		this.content = content;
		this.requestId = requestId;
	}

	public String asString() {
		return DomUtil.asString(content);
	}
	
	public Document asDocument() {
		return content;
	}

	public String getRequestId() {
		return requestId;
	}	
}
