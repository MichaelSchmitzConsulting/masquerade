package masquerade.sim.channel.jms;

import java.lang.reflect.Method;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

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

	private final class MqConnectionFactoryWrapper implements ConnectionFactory {
		private final Method createConnectionMethod;
		private final Method createConnectionUserPwdMethod;
		private final Object factory;

		/**
		 * @param createConnectionMethod
		 * @param createConnectionUserPwdMethod
		 * @param factory
		 */
		public MqConnectionFactoryWrapper(Method createConnectionMethod, Method createConnectionUserPwdMethod, Object factory) {
			this.createConnectionMethod = createConnectionMethod;
			this.createConnectionUserPwdMethod = createConnectionUserPwdMethod;
			this.factory = factory;
		}

		@Override public Connection createConnection(String user, String password) throws JMSException {
			try {
				return (Connection) createConnectionUserPwdMethod.invoke(factory, user, password);
			} catch (Exception e) {
				throw new JMSException(e.getMessage());
			}
		}

		@Override public Connection createConnection() throws JMSException {
			try {
				return (Connection) createConnectionMethod.invoke(factory);
			} catch (Exception e) {
				throw new JMSException(e.getMessage());
			}
		}
	}

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
		Object factory = factoryType.newInstance();

		// Get constant value for transport type
		Class<?> constantType = ClassUtil.load("com.ibm.mq.jms.JMSC");
		Integer directTcpIp = (Integer) constantType.getField("MQJMS_TP_DIRECT_TCPIP").get(null);

		// Set transport type, host and port
		factoryType.getMethod("setTransportType", int.class).invoke(factory, directTcpIp);
		factoryType.getMethod("setHostName", String.class).invoke(factory, host);
		factoryType.getMethod("setPort", int.class).invoke(factory, port);
		factoryType.getMethod("setChannel", String.class).invoke(factory, channel);
		factoryType.getMethod("setQueueManager", String.class).invoke(factory, queueManager);

		Method createConnectionMethod = factoryType.getMethod("createConnection");
		Method createConnectionUserPwdMethod = factoryType.getMethod("createConnection", String.class, String.class);
		
		return new MqConnectionFactoryWrapper(createConnectionMethod, createConnectionUserPwdMethod, factory);
	}
}
