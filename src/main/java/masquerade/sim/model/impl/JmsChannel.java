package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.jms.ConnectionFactoryProvider;
import masquerade.sim.channel.jms.JmsChannelListener;
import masquerade.sim.model.ChannelListener;

/**
 * A channel receiving requests from a JMS queue or topic
 */
public abstract class JmsChannel extends AbstractChannel {

	private String user = "";
	private String password = "";
	private String destinationName = "";
	private String responseDestinationName = "";
	private boolean isTopic = false;

	public JmsChannel(String name) {
		super(name);
	}

	public abstract Class<? extends ConnectionFactoryProvider> connectionFactoryProvider();
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
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

	@Override
	public boolean isActive() {
		return isNotEmpty(destinationName);
	}

	@Override
	public Class<? extends ChannelListener<?>> listenerType() {
		return JmsChannelListener.class;
	}
}
