package masquerade.sim.channel.jms;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.JmsChannel;
import masquerade.sim.model.impl.WebSphereMqJmsChannel;

/**
 * Provides {@link ConnectionFactory} instances for IBM WebSphere MQ connections over TCP/IP.
 * Is configured using a {@link WebSphereMqJmsChannel}.
 */
public class WSMQConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final Logger log = Logger.getLogger(WSMQConnectionFactoryProvider.class.getName());

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel) {
		try {
			WebSphereMqJmsChannel mqChannel = (WebSphereMqJmsChannel) channel;
			return getMqConnFactory(mqChannel.getHost(), mqChannel.getPort(), mqChannel.getChannel(), mqChannel.getQueueManager());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Unable to create WSMQ connection factory", e);
			return null;
		}
	}

	private ConnectionFactory getMqConnFactory(String host, int port, String channel, String queueManager) throws Exception {
		// Create factory
		Class<?> factoryType = Class.forName("com.ibm.mq.jms.MQConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) factoryType.newInstance();

		// Get constant value for transport type
		Class<?> constantType = Class.forName("com.ibm.mq.jms.JMSC");
		Integer directTcpIp = (Integer) constantType.getField("MQJMS_TP_DIRECT_TCPIP").get(null);

		// Set transport type, host and port
		factoryType.getMethod("setTransportType", Integer.class).invoke(factory, directTcpIp);
		factoryType.getMethod("setHostName", String.class).invoke(factory, host);
		factoryType.getMethod("setPort", Integer.class).invoke(factory, port);
		factoryType.getMethod("setChannel", String.class).invoke(factory, channel);
		factoryType.getMethod("setQueueManager", String.class).invoke(factory, queueManager);

		return factory;
	}
}
