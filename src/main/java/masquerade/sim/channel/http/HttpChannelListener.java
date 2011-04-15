package masquerade.sim.channel.http;

import java.io.InputStream;
import java.io.OutputStream;

import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.HttpChannel;
import masquerade.sim.util.DomUtil;
import masquerade.sim.util.StringUtil;

import org.w3c.dom.Document;

public class HttpChannelListener extends AbstractChannelListener<HttpChannel> {

	private String contentType;
	private String location;

	public void processRequest(String clientInfo, InputStream content, OutputStream servletOutputStream) throws Exception {
		Document doc = DomUtil.parse(content);
		processRequest(clientInfo, doc, servletOutputStream);
	}
	
	@Override
	protected synchronized void onStart(HttpChannel channel) {
		this.contentType = channel.getContentType();
		this.location = StringUtil.removeLeadingSlash(channel.getLocation());
	}
	
	@Override
	protected synchronized void onStop() {
		contentType = null;
	}

	public String getContentType() {
		return contentType;
	}
	
	public boolean locationMatches(String location) {
		return location.equals(this.location);
	}
}
