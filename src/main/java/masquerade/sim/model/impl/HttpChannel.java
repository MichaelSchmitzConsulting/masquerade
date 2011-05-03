package masquerade.sim.model.impl;

import masquerade.sim.channel.http.HttpChannelListener;
import masquerade.sim.model.ChannelListener;

/**
 * A channel that receives HTTP requests
 */
public class HttpChannel extends AbstractChannel {
	
	private String location = "/exam/ple";
	private String responseContentType = "text/xml";

	public HttpChannel(String name) {
		super(name);
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getResponseContentType() {
		return responseContentType;
	}

	public void setResponseContentType(String contentType) {
		this.responseContentType = contentType;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public Class<? extends ChannelListener<?>> listenerType() {
		return HttpChannelListener.class;
	}

	@Override
	public String toString() {
		return "HTTP: " + location;
	}
}
