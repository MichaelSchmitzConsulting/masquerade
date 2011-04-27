package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.lang.reflect.Constructor;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.DefaultJmsChannel;
import masquerade.sim.model.impl.JmsChannel;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.ClassUtil;

/**
 * {@link ConnectionFactoryProvider} able to provide JMS {@link ConnectionFactory} instances
 * for Apache Active MQ. 
 */
public class ActiveMqConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final StatusLog log = StatusLogger.get(ActiveMqConnectionFactoryProvider.class);

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel) {
		DefaultJmsChannel jmsChannel = (DefaultJmsChannel) channel;
		
		String user = jmsChannel.getUser();
		String password = jmsChannel.getPassword();
		String url = jmsChannel.getUrl();

		if (isEmpty(user)) {
			user = null;
		}
		if (isEmpty(password)) {
			password = null;
		}
		
		try {
			return createFactory(user, password, url);
		} catch (Exception e) {
			log.error("Unable to create WSMQ connection factory", e);
			return null;
		}
	}

	private ConnectionFactory createFactory(String user, String password, String url) throws Exception {
		Class<?> factoryType;
		try {
			factoryType = ClassUtil.load("org.apache.activemq.ActiveMQConnectionFactory");
		} catch (ClassNotFoundException e) {
			log.error("Unable to load ActiveMQConnectionFactory - please place the ActiveMQ JARs in your classpath");
			return null;
		}
		Constructor<?> constructor = factoryType.getConstructor(String.class, String.class, String.class);
		return (ConnectionFactory) constructor.newInstance(user, password, url);
	}
}
