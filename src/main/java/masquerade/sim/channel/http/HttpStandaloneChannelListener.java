package masquerade.sim.channel.http;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.HttpStandaloneChannel;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * {@link ChannelListener} starting a Jetty web server on the specified port to 
 * receive HTTP requests.
 */
public class HttpStandaloneChannelListener extends AbstractChannelListener<HttpStandaloneChannel> {

	private static final Logger log = Logger.getLogger(HttpStandaloneChannelListener.class.getName());
	
	private Server server;

	/**
	 * Starts a Jetty server on the port specified in the channel,
	 * processing requests for the location for this channel.
	 */
	@Override
	protected synchronized void onStart(HttpStandaloneChannel channel) {
		onStop();
		
		String contentType = channel.getContentType();
		String location = StringUtil.removeLeadingSlash(channel.getLocation());
		int port = channel.getPort();
		
	    server = new Server(port);
	    server.setHandler(new RequestHandler(contentType, location));
		try {
			server.start();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Unable to start standalone HTTP server on port " + port, e);
			server = null;
		}
	}

	/**
	 * Stops Jetty
	 */
	@Override
	protected synchronized void onStop() {
		if (server != null) {
			try {
				server.stop();
				server.join();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Unable to stop standalone HTTP server", e);
			}
			server = null;
		}
	}
	
	/**
	 * Jetty handler for requests matching the {@link HttpStandaloneChannel} 
	 * specification.
	 */
	private class RequestHandler extends AbstractHandler {
		private String contentType;
		private String location;
		
		/**
		 * @param contentType Response content type
		 * @param location Request path (e.g. myService)
		 */
		public RequestHandler(String contentType, String location) {
			this.contentType = contentType;
			this.location = location;
		}

		/**
		 * Handle request if it matches {@link #location}, return 404 otherwise.
		 */
		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			String pathInfo = HttpUtil.requestUrlName(request.getPathInfo());
			if (this.location.equals(pathInfo)) {
				internalHandleRequest(request, response);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		}

		/**
		 * Handle request using superclass. Return HTTP status code 500 on exception. 
		 * @param request
		 * @param response
		 * @throws IOException
		 */
		private void internalHandleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
			Object requestContent = IOUtils.toString(request.getInputStream());
			try {
				response.setContentType(contentType);
				processRequest(HttpUtil.clientInfo(request), requestContent, response.getOutputStream());
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Exception in standalone HTTP request handler", e);
				response.reset();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				PrintStream stream = new PrintStream(response.getOutputStream());
				e.printStackTrace(stream);
				stream.flush();
			}
		}		
	}
}
