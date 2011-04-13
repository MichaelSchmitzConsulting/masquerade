package masquerade.sim.model.impl;

import masquerade.sim.model.ChannelListener;


/**
 * A channel that receives HTTP requests
 */
public class HttpChannel extends AbstractChannel {
	
	private String location = "/exam/ple";
	private String contentType = "text/xml";

	public HttpChannel(String name) {
		super(name);
	}

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public Class<? extends ChannelListener<?>> getListenerType() {
		return HttpChannelListener.class;
	}

	@Override
	public String toString() {
		return "HTTP: " + location;
	}
}
