package masquerade.sim.model.impl;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

import masquerade.sim.util.DomUtil;

import org.w3c.dom.Document;

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

	public void processPost(String clientInfo, InputStream content, ServletOutputStream servletOutputStream) throws Exception {
		Document doc = DomUtil.parse(content);
		processRequest(clientInfo, doc, servletOutputStream);
	}
	
	@Override
	protected void marshalResponse(Object response, OutputStream responseOutput) {
		Document doc = (Document) response;
		DomUtil.write(doc, responseOutput);
	}

	@Override
	public String toString() {
		return "HTTP: " + location;
	}
}
