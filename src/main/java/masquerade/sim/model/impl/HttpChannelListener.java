package masquerade.sim.model.impl;

import java.io.InputStream;
import java.io.OutputStream;

import masquerade.sim.util.DomUtil;
import masquerade.sim.util.StringUtil;

import org.w3c.dom.Document;

public class HttpChannelListener extends AbstractChannelListener<HttpChannel> {

	private volatile HttpChannel channel;
	private String contentType;
	private String location;

	public void processRequest(String clientInfo, InputStream content, OutputStream servletOutputStream) throws Exception {
		if (channel != null) {
			Document doc = DomUtil.parse(content);
			processRequest(channel.getRequestMappings(), clientInfo, doc, servletOutputStream);
		}
	}
	
	@Override
	protected void onStart(HttpChannel channel) {
		this.channel = channel;
		this.contentType = channel.getContentType();
		this.location = StringUtil.removeLeadingSlash(channel.getLocation());
	}
	
	@Override
	protected void onStop() {
		channel = null;
		contentType = null;
	}

	public String getContentType() {
		return contentType;
	}
	
	public boolean locationMatches(String location) {
		return location.equals(this.location);
	}
}
