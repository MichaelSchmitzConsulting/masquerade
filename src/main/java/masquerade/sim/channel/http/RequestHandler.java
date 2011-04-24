package masquerade.sim.channel.http;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.impl.HttpStandaloneChannel;
import masquerade.sim.model.impl.RequestProcessor;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Jetty handler for requests matching the {@link HttpStandaloneChannel} 
 * specification.
 */
class RequestHandler extends AbstractHandler {
	private static final StatusLog log = StatusLogger.get(RequestHandler.class.getName());
	
	private String contentType;
	private Map<String, RequestProcessor> requestProcessors = new HashMap<String, RequestProcessor>();
	
	/**
	 * @param contentType Response content type
	 */
	public RequestHandler(String contentType) {
		this.contentType = contentType;
	}

	public void addRequestProcessor(String location, RequestProcessor requestProcessor) {
		synchronized (requestProcessors) {
			requestProcessors.put(location, requestProcessor);
		}
	}

	/**
	 * Removes a request processor
	 * @param location
	 * @return <code>true</code> if this request processor was the last processor for this handler, and the server doesn't contain any more request handlers
	 */
	public boolean removeRequestProcessor(String location) {
		synchronized (requestProcessors) {
			requestProcessors.remove(location);
			return requestProcessors.isEmpty();
		}
	}
	
	/**
	 * Handle request if it matches {@link #location}, return 404 otherwise.
	 */
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String pathInfo = HttpUtil.requestUrlName(request.getPathInfo());
		RequestProcessor processor = getRequestProcessor(pathInfo);
		if (processor != null) {
			try {
				internalHandleRequest(request, response, processor);
			} catch (Throwable t) {
				log.error("Exception in request handler", t);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				t.printStackTrace(new PrintWriter(response.getOutputStream()));
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		baseRequest.setHandled(true);
	}

	private RequestProcessor getRequestProcessor(String pathInfo) {
		synchronized (requestProcessors) {
			return requestProcessors.get(pathInfo);
		}
	}

	/**
	 * Handle request using superclass. Return HTTP status code 500 on exception. 
	 * @param request
	 * @param response
	 * @param processor 
	 * @throws IOException
	 */
	private void internalHandleRequest(HttpServletRequest request, HttpServletResponse response, RequestProcessor processor) throws IOException {
		Object requestContent = IOUtils.toString(request.getInputStream());
		try {
			response.setContentType(contentType);
			processor.processRequest(HttpUtil.clientInfo(request), requestContent, response.getOutputStream());
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in standalone HTTP request handler", e);
			response.reset();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			PrintStream stream = new PrintStream(response.getOutputStream());
			e.printStackTrace(stream);
			stream.flush();
		}
	}		
}