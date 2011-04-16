package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.lang.reflect.Constructor;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.JmsChannel;

/**
 * {@link ConnectionFactoryProvider} able to provide JMS {@link ConnectionFactory} instances
 * for Apache Active MQ. 
 */
public class ActiveMqConnectionFactoryProvider implements ConnectionFactoryProvider {

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel) {
		String user = channel.getUser();
		String password = channel.getPassword();
		String url = channel.getUrl();

		if (isEmpty(user)) {
			user = null;
		}
		if (isEmpty(password)) {
			password = null;
		}
		
		try {
			return createFactory(user, password, url);
		} catch (Exception e) {
			return null;
		}
	}

	private ConnectionFactory createFactory(String user, String password, String url) throws Exception {
		Class<?> factoryType = Class.forName("org.apache.activemq.ActiveMQConnectionFactory");
		Constructor<?> constructor = factoryType.getConstructor(String.class, String.class, String.class);
		return (ConnectionFactory) constructor.newInstance(user, password, url);
	}
}
