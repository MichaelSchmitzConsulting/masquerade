package masquerade.sim.channel.http.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import masquerade.sim.channel.http.HttpChannel;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.ResponseDestination;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.DefaultResponseDestination;
import masquerade.sim.util.DomUtil;
import masquerade.sim.util.StringUtil;

import org.w3c.dom.Document;

/**
 * A HTTP channel listener using the servlet container's web transport 
 * to provide an endpoint.
 * 
 * @see HttpChannelServlet
 */
public class HttpChannelListener extends AbstractChannelListener<HttpChannel> {

	private volatile String contentType; // Used in request handling threads
	private volatile String location; // Used in request handling threads

	public void processRequest(String clientInfo, InputStream content, OutputStream servletOutputStream) throws Exception {
		Document doc = DomUtil.parse(content);
		ResponseDestination responseDestination = new DefaultResponseDestination(servletOutputStream);
		processRequest(clientInfo, doc, responseDestination, new Date());
	}
	
	@Override
	protected synchronized void onStart(HttpChannel channel, ChannelListenerContext context) {
		VariableHolder config = context.getVariableHolder();
		
		this.contentType = config.substituteVariables(channel.getResponseContentType());
		this.location = config.substituteVariables(
			StringUtil.removeLeadingSlash(channel.getLocation()));
	}
	
	@Override
	protected synchronized void onStop(ChannelListenerContext context) { }

	public String getContentType() {
		return contentType;
	}
	
	public boolean locationMatches(String location) {
		return location.equals(this.location);
	}
}
