package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.jms.JmsChannelListener;
import masquerade.sim.model.ChannelListener;

/**
 * A channel receiving requests from a JMS queue
 */
public class JmsChannel extends AbstractChannel {

	private String url = "";
	private String user = "";
	private String password = "";
	private String destinationName = "";
	private String responseDestinationName = "";
	private boolean isTopic = false;

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

	/**
	 * @return JMS destination name (queue/topic name)
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * @param destinationName The destination name to set
	 */
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	/**
	 * @return the responseDestinationName
	 */
	public String getResponseDestinationName() {
		return responseDestinationName;
	}

	/**
	 * @param responseDestinationName the responseDestinationName to set
	 */
	public void setResponseDestinationName(String responseDestinationName) {
		this.responseDestinationName = responseDestinationName;
	}

	/**
	 * @return SimpleMessageListenerContainer
	 */
	public boolean isTopic() {
		return isTopic;
	}

	/**
	 * @param isTopic SimpleMessageListenerContainer
	 */
	public void setTopic(boolean isTopic) {
		this.isTopic = isTopic;
	}

	/**
	 * @return <code>true</code> If this channel is configured with an URL and a destination
	 */
	@Override
	public boolean isActive() {
		return isNotEmpty(url) && isNotEmpty(destinationName);
	}

	@Override
	public String toString() {
		return "JmsChannel: " + user + "@" + url;
	}

	@Override
	public Class<? extends ChannelListener<?>> getListenerType() {
		return JmsChannelListener.class;
	}
}
