	package masquerade.sim.channel.http;

import java.util.logging.Level;
import java.util.logging.Logger;


import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.HttpStandaloneChannel;
import masquerade.sim.model.impl.RequestProcessor;
import masquerade.sim.util.StringUtil;

import org.eclipse.jetty.server.Server;

/**
 * {@link ChannelListener} starting a Jetty web server on the specified port to 
 * receive HTTP requests.
 */
public class HttpStandaloneChannelListener extends AbstractChannelListener<HttpStandaloneChannel> {

	static final Logger log = Logger.getLogger(HttpStandaloneChannelListener.class.getName());
	
	private Server server;
	private int port;
	private String location;

	/**
	 * Starts a Jetty server on the port specified in the channel,
	 * processing requests for the location for this channel.
	 */
	@Override
	protected synchronized void onStart(HttpStandaloneChannel channel) {
		onStop();
		
		String contentType = channel.getContentType();
		location = StringUtil.removeLeadingSlash(channel.getLocation());
		port = channel.getPort();
		
		String serverAttributeKey = serverAttributeKey(port);
		Server startedServer = getContext().getAttribute(serverAttributeKey);
		RequestHandler handler;
		// No server started yet on this port?
		if (startedServer == null) {
			// Create a new Jetty instance for this port
			startedServer = new Server(port);
			handler = new RequestHandler(contentType);
			startedServer.setHandler(handler);
		
			try {
				// Start Jetty, add to context for reuse by other listeners on the same port 
				startedServer.start();
				getContext().setAttribute(serverAttributeKey, startedServer);
				this.server = startedServer;
			} catch (Exception e) {
				log.log(Level.SEVERE, "Unable to start standalone HTTP server on port " + port, e);
				startedServer = null;
			}
		} else {
			// Server already started on this port, reuse
			handler = (RequestHandler) startedServer.getHandler();
		}
		
		// Add this channel listener as a request processor for the specified location
		handler.addRequestProcessor(location, (RequestProcessor) this);
	}

	/**
	 * Stops Jetty
	 */
	@Override
	protected synchronized void onStop() {
		if (server != null) {
			synchronized (server) {
				RequestHandler handler = (RequestHandler) server.getHandler();
				if (handler.removeRequestProcessor(location)) {
					try {
						getContext().removeAttribute(serverAttributeKey(port));
						server.stop();
						server.join();
					} catch (Exception e) {
						log.log(Level.SEVERE, "Unable to stop standalone HTTP server", e);
					}
				}
				server = null;
				location = null;
			}
		}
	}
	
	private String serverAttributeKey(int port) {
		return getClass().getName() + "-server-" + port;
	}
}
