package masquerade.sim.channel.http.impl;

import masquerade.sim.channel.http.HttpStandaloneChannel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.RequestProcessor;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;

import org.eclipse.jetty.server.Server;

/**
 * {@link ChannelListener} starting a Jetty web server on the specified port to 
 * receive HTTP requests.
 */
public class HttpStandaloneChannelListener extends AbstractChannelListener<HttpStandaloneChannel> {

	static final StatusLog log = StatusLogger.get(HttpStandaloneChannelListener.class);
	
	private Server server;
	private int port;
	private String location;

	private String name;

	/**
	 * Starts a Jetty server on the port specified in the channel,
	 * processing requests for the location for this channel.
	 */
	@Override
	protected synchronized void onStart(HttpStandaloneChannel channel, ChannelListenerContext context) {
		onStop(context);
		
		VariableHolder config = context.getVariableHolder();
		
		String contentType = config.substituteVariables(channel.getResponseContentType());
		location = config.substituteVariables(
				StringUtil.removeLeadingSlash(channel.getLocation()));
		
		port = channel.getPort();
		name = channel.getName();
		
		String serverAttributeKey = serverAttributeKey(port);
		Server startedServer = context.getAttribute(serverAttributeKey);
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
				context.setAttribute(serverAttributeKey, startedServer);
			} catch (Exception e) {
				log.error("Unable to start standalone HTTP server on port " + port, e);
				startedServer = null;
			}
		} else {
			// Server already started on this port, reuse
			handler = (RequestHandler) startedServer.getHandler();
		}
		this.server = startedServer;
		
		// Add this channel listener as a request processor for the specified location
		handler.addRequestProcessor(location, (RequestProcessor) this);
	}

	/**
	 * Stops Jetty
	 */
	@Override
	protected synchronized void onStop(ChannelListenerContext context) {
		if (server != null) {
			synchronized (server) {
				log.info("Stopping HTTP standalone channel " + name);
				RequestHandler handler = (RequestHandler) server.getHandler();
				if (handler.removeRequestProcessor(location)) {
					try {
						log.info("Stopping HTTP standalone server on port " + port);
						context.removeAttribute(serverAttributeKey(port));
						server.stop();
						server.join();
					} catch (Exception e) {
						log.error("Unable to stop standalone HTTP server", e);
					}
				}
				server = null;
				location = null;
				port = 0;
				name = null;
			}
		}
	}
	
	private String serverAttributeKey(int port) {
		return getClass().getName() + "-server-" + port;
	}
}
