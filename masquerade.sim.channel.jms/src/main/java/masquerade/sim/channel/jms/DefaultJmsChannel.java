package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.jms.impl.ActiveMqConnectionFactoryProvider;
import masquerade.sim.channel.jms.impl.ConnectionFactoryProvider;

/** 
 * A {@link masquerade.sim.model.Channel} receiving messages from an MQ
 * provider that can be initialized with URL, user and password. 
 */
public class DefaultJmsChannel extends JmsChannel {

	private String url = "";
	private Class<? extends ConnectionFactoryProvider> connectionFactoryProvider = ActiveMqConnectionFactoryProvider.class;

	public DefaultJmsChannel(String id) {
		super(id);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Class<? extends ConnectionFactoryProvider> getConnectionFactoryProvider() {
		return connectionFactoryProvider;
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
}
