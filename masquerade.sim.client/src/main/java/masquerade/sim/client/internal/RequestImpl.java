package masquerade.sim.client.internal;

import masquerade.sim.client.Request;

public class RequestImpl implements Request {

	private final String requestId;
	private final String content;

	public RequestImpl(String requestId, String content) {
		this.requestId = requestId;
		this.content = content;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public String getContent() {
		return content;
	}
}
