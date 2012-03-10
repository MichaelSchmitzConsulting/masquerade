package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.jms.impl.ConnectionFactoryProvider;
import masquerade.sim.channel.jms.impl.WSMQConnectionFactoryProvider;
import masquerade.sim.model.Channel;

/**
 * A JMS {@link Channel} receiving messages from a WebSphere MQ server over TCP/IP
 */
public class WebSphereMqJmsChannel extends JmsChannel {

	private String host = "localhost";
	private int port = 1414;
	private String channel = "SYSTEM.DEF.SVRCONN";
	private String queueManager = "QM_localhost";

	public WebSphereMqJmsChannel(String id) {
		super(id);
	}

	@Override
	public boolean isActive() {
		return super.isActive() && isNotEmpty(host);
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the queueManager
	 */
	public String getQueueManager() {
		return queueManager;
	}

	/**
	 * @param queueManager the queueManager to set
	 */
	public void setQueueManager(String queueManager) {
		this.queueManager = queueManager;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public Class<? extends ConnectionFactoryProvider> connectionFactoryProvider() {
		return WSMQConnectionFactoryProvider.class;
	}
}
