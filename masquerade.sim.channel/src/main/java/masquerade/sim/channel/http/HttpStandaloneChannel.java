package masquerade.sim.channel.http;

import masquerade.sim.channel.http.impl.HttpStandaloneChannelListener;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;

public class HttpStandaloneChannel extends HttpChannel {

	private static final int NOT_SET = 0;
	
	private int port;
	
	public HttpStandaloneChannel(String name) {
		super(name);
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public Class<? extends ChannelListener<? extends Channel>> listenerType() {
		return HttpStandaloneChannelListener.class;
	}

	@Override
	public boolean isActive() {
		return super.isActive() && port != NOT_SET;
	}

}
