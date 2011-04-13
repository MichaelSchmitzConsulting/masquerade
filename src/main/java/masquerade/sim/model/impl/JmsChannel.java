package masquerade.sim.model.impl;

import masquerade.sim.model.ChannelListener;

/**
 * A channel receiving requests from a JMS queue
 */
public class JmsChannel extends AbstractChannel {

	private String url;
	private String user;
	private String password;

	public JmsChannel(String name) {
		super(name);
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "JmsChannel: " + user + "@" + url;
	}

	@Override
	public Class<? extends ChannelListener<?>> getListenerType() {
		// TODO JMS Channel listener
		return null;
	}
}
