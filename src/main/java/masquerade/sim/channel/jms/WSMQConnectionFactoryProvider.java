package masquerade.sim.channel.jms;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.DefaultJmsChannel;
import masquerade.sim.model.impl.JmsChannel;
import masquerade.sim.model.impl.WebSphereMqJmsChannel;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.ClassUtil;

/**
 * Provides {@link ConnectionFactory} instances for IBM WebSphere MQ connections over TCP/IP.
 * Is configured using a {@link WebSphereMqJmsChannel}. Not an implementation of
 * {@link ConnectionFactoryProvider} because it does not support the default JMS channel
 * attributes provided by {@link DefaultJmsChannel}.
 */
public class WSMQConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final StatusLog log = StatusLogger.get(WSMQConnectionFactoryProvider.class);

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel) {
		try {
			WebSphereMqJmsChannel mqChannel = (WebSphereMqJmsChannel) channel;
			return getMqConnFactory(mqChannel.getHost(), mqChannel.getPort(), mqChannel.getChannel(), mqChannel.getQueueManager());
		} catch (Exception e) {
			log.error("Unable to create WSMQ connection factory", e);
			return null;
		}
	}

	private ConnectionFactory getMqConnFactory(String host, int port, String channel, String queueManager) throws Exception {
		// Create factory
		Class<?> factoryType;
		try {
			factoryType = ClassUtil.load("com.ibm.mq.jms.MQConnectionFactory");
		} catch (ClassNotFoundException e) {
			log.error("Unable to load MQConnectionFactory - please place WebSphere MQ jars in your classpath");
			return null;
		}
		ConnectionFactory factory = (ConnectionFactory) factoryType.newInstance();

		// Get constant value for transport type
		Class<?> constantType = ClassUtil.load("com.ibm.mq.jms.JMSC");
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
