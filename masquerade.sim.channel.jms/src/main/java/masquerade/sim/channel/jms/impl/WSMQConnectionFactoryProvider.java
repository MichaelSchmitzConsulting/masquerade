package masquerade.sim.channel.jms.impl;

import java.lang.reflect.Method;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import masquerade.sim.channel.jms.DefaultJmsChannel;
import masquerade.sim.channel.jms.JmsChannel;
import masquerade.sim.channel.jms.WebSphereMqJmsChannel;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * Provides {@link ConnectionFactory} instances for IBM WebSphere MQ connections over TCP/IP.
 * Is configured using a {@link WebSphereMqJmsChannel}. Not an implementation of
 * {@link ConnectionFactoryProvider} because it does not support the default JMS channel
 * attributes provided by {@link DefaultJmsChannel}.
 */
public class WSMQConnectionFactoryProvider implements ConnectionFactoryProvider {
	
	private static final String QUEUE_FACTORY_CLASS_NAME = "com.ibm.mq.jms.MQQueueConnectionFactory";
	private static final String TOPIC_FACTORY_CLASS_NAME = "com.ibm.mq.jms.MQTopicConnectionFactory";
	private static final int MQJMS_TP_CLIENT = 1;

	private final static class MqConnectionFactoryWrapper implements ConnectionFactory {
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
				throw convert(e);
			}
		}

		@Override public Connection createConnection() throws JMSException {
			try {
				return (Connection) createConnectionMethod.invoke(factory);
			} catch (Exception e) {
				throw convert(e);
			}
		}

		private static JMSException convert(Exception e) {
			Throwable cause = e.getCause();
			if (cause instanceof JMSException) {
				return (JMSException) cause;
			} else if (cause != null) {
				StatusLog log = StatusLogger.get(WSMQConnectionFactoryProvider.class);
				log.error("Exception in MqConnectionFactoryWrapper", cause);
				return new JMSException(cause.getClass().getName() + ": " + cause.getMessage());
			} else {
				StatusLog log = StatusLogger.get(WSMQConnectionFactoryProvider.class);
				log.error("Exception with no cause in MqConnectionFactoryWrapper", e);
				return new JMSException(e.getClass().getName() + ": " + e.getMessage());
			}
		}
	}

	private static final StatusLog log = StatusLogger.get(WSMQConnectionFactoryProvider.class);

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel, ChannelListenerContext context) {
		try {
			WebSphereMqJmsChannel mqChannel = (WebSphereMqJmsChannel) channel;
			
			VariableHolder config = context.getVariableHolder();
			
			String host = config.substituteVariables(mqChannel.getHost());
			int port = mqChannel.getPort();
			String connectionChannel = config.substituteVariables(mqChannel.getChannel());
			String queueManager = config.substituteVariables(mqChannel.getQueueManager());
			
			return getMqConnFactory(mqChannel.isTopic(), host, port, connectionChannel, queueManager, context.getClassLoader());
		} catch (Exception e) {
			log.error("Unable to create WSMQ connection factory", e);
			return null;
		}
	}

	private ConnectionFactory getMqConnFactory(boolean isTopic, String host, int port, String channel, String queueManager, ClassLoader classLoader) throws Exception {
		// Create factory
		Class<?> factoryType;
		try {
			String className = isTopic ? TOPIC_FACTORY_CLASS_NAME : QUEUE_FACTORY_CLASS_NAME;
			factoryType = classLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			log.error("Unable to load MQConnectionFactory - please place WebSphere MQ jars in your classpath", e);
			return null;
		}
		Object factory = factoryType.newInstance();

		// Set transport type, host and port
		factoryType.getMethod("setTransportType", int.class).invoke(factory, MQJMS_TP_CLIENT);
		factoryType.getMethod("setHostName", String.class).invoke(factory, host);
		//factoryType.getMethod("setPort", int.class).invoke(factory, port); TODO: Put back in and test
		factoryType.getMethod("setChannel", String.class).invoke(factory, channel);
		factoryType.getMethod("setQueueManager", String.class).invoke(factory, queueManager);

		String createMethodName = isTopic ? "createTopicConnection" : "createQueueConnection";
		Method createConnectionMethod = factoryType.getMethod(createMethodName);
		Method createConnectionUserPwdMethod = factoryType.getMethod(createMethodName, String.class, String.class);
		
		return new MqConnectionFactoryWrapper(createConnectionMethod, createConnectionUserPwdMethod, factory);
	}
}
