package masquerade.sim.channel.jms.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.lang.reflect.Constructor;

import javax.jms.ConnectionFactory;

import masquerade.sim.channel.jms.DefaultJmsChannel;
import masquerade.sim.channel.jms.JmsChannel;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * {@link ConnectionFactoryProvider} able to provide JMS {@link ConnectionFactory} instances
 * for Apache Active MQ. 
 */
public class ActiveMqConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final String FACTORY_CLASS_NAME = "org.apache.activemq.ActiveMQConnectionFactory";
	private static final StatusLog log = StatusLogger.get(ActiveMqConnectionFactoryProvider.class);

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel, ChannelListenerContext context) {
		DefaultJmsChannel jmsChannel = (DefaultJmsChannel) channel;
		
		VariableHolder config = context.getVariableHolder();
		
		String user = config.substituteVariables(jmsChannel.getUser());
		String password = config.substituteVariables(jmsChannel.getPassword());
		String url = config.substituteVariables(jmsChannel.getUrl());

		if (isEmpty(user)) {
			user = null;
		}
		if (isEmpty(password)) {
			password = null;
		}
		
		try {
			return createFactory(user, password, url);
		} catch (Exception e) {
			log.error("Unable to create ActiveMQ connection factory", e);
			return null;
		}
	}

	private ConnectionFactory createFactory(String user, String password, String url) throws Exception {
		Class<?> factoryType;
		try {
			factoryType = getClass().getClassLoader().loadClass(FACTORY_CLASS_NAME);
		} catch (ClassNotFoundException e) {
			log.error("Unable to load ActiveMQConnectionFactory - please place the ActiveMQ JARs in your classpath");
			return null;
		}
		Constructor<?> constructor = factoryType.getConstructor(String.class, String.class, String.class);
		return (ConnectionFactory) constructor.newInstance(user, password, url);
	}
}
