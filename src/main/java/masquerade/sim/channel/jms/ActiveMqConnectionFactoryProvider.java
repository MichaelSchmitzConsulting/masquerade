package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.DefaultJmsChannel;
import masquerade.sim.model.impl.JmsChannel;

/**
 * {@link ConnectionFactoryProvider} able to provide JMS {@link ConnectionFactory} instances
 * for Apache Active MQ. 
 */
public class ActiveMqConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final Logger log = Logger.getLogger(ActiveMqConnectionFactoryProvider.class.getName());

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
			log.log(Level.SEVERE, "Unable to create WSMQ connection factory", e);
			return null;
		}
	}

	private ConnectionFactory createFactory(String user, String password, String url) throws Exception {
		Class<?> factoryType = Class.forName("org.apache.activemq.ActiveMQConnectionFactory");
		Constructor<?> constructor = factoryType.getConstructor(String.class, String.class, String.class);
		return (ConnectionFactory) constructor.newInstance(user, password, url);
	}
}
