package masquerade.sim.model.impl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import masquerade.sim.util.DomUtil;
import masquerade.sim.util.StringUtil;

import org.w3c.dom.Document;

public class HttpChannelListener extends AbstractChannelListener<HttpChannel> {

	private volatile HttpChannel channel;
	private String contentType;
	private String location;

	public void processRequest(String clientInfo, InputStream content, ServletOutputStream servletOutputStream) throws Exception {
		if (channel != null) {
			Document doc = DomUtil.parse(content);
			processRequest(channel, clientInfo, doc, servletOutputStream);
		}
	}
	
	@Override
	protected void marshalResponse(Object response, OutputStream responseOutput) {
		// TODO: Marshalling
		Document doc = (Document) response;
		DomUtil.write(doc, responseOutput);
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
