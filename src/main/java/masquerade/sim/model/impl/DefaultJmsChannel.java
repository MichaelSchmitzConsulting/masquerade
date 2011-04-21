package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.jms.ActiveMqConnectionFactoryProvider;
import masquerade.sim.channel.jms.ConnectionFactoryProvider;

/** 
 * A {@link masquerade.sim.model.Channel} receiving messages from an MQ
 * provider that can be initialized with URL, user and password. 
 */
public class DefaultJmsChannel extends JmsChannel {

	private String url = "";
	private Class<? extends ConnectionFactoryProvider> connectionFactoryProvider = ActiveMqConnectionFactoryProvider.class;

	public DefaultJmsChannel(String name) {
		super(name);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Class<? extends ConnectionFactoryProvider> getConnectionFactoryProvider() {
		return connectionFactoryProvider ;
	}

	/**
	 * @param connectionFactoryProvider the connectionFactoryProvider to set
	 */
	public void setConnectionFactoryProvider(Class<? extends ConnectionFactoryProvider> connectionFactoryProvider) {
		this.connectionFactoryProvider = connectionFactoryProvider;
	}

	@Override
	public Class<? extends ConnectionFactoryProvider> connectionFactoryProvider() {
		return getConnectionFactoryProvider();
	}

	/**
	 * @return <code>true</code> If this channel is configured with an URL and a destination
	 */
	@Override
	public boolean isActive() {
		return super.isActive() && isNotEmpty(url) && isNotEmpty(getDestinationName()) && connectionFactoryProvider != null;
	}

	@Override
	public String toString() {
		return "Default JMS Channel " + getUrl();
	}
}
