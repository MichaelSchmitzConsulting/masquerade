package masquerade.sim.channel.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.HttpChannel;
import masquerade.sim.util.DomUtil;
import masquerade.sim.util.StringUtil;

import org.w3c.dom.Document;

public class HttpChannelListener extends AbstractChannelListener<HttpChannel> {

	private volatile String contentType; // Used in request handling threads
	private volatile String location; // Used in request handling threads

	public void processRequest(String clientInfo, InputStream content, OutputStream servletOutputStream) throws Exception {
		Document doc = DomUtil.parse(content);
		processRequest(clientInfo, doc, servletOutputStream, new Date());
	}
	
	@Override
	protected synchronized void onStart(HttpChannel channel) {
		VariableHolder config = getContext().getVariableHolder();
		
		this.contentType = config.substituteVariables(channel.getResponseContentType());
		this.location = config.substituteVariables(
			StringUtil.removeLeadingSlash(channel.getLocation()));
	}
	
	@Override
	protected synchronized void onStop() { }

	public String getContentType() {
		return contentType;
	}
	
	public boolean locationMatches(String location) {
		return location.equals(this.location);
	}
}
