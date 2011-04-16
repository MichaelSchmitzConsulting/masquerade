package masquerade.sim.channel.jms;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.JmsChannel;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMqConnectionFactoryProvider implements ConnectionFactoryProvider {

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel) {
		String user = channel.getUser();
		String password = channel.getPassword();
		String url = channel.getUrl();

		return new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
			ActiveMQConnection.DEFAULT_BROKER_URL);
/*
		if (isNotEmpty(user) && isNotEmpty(password)) {
			return new ActiveMQConnectionFactory(user, password, url);
		} else {
			return new ActiveMQConnectionFactory(url);
		}
*/
	}
}
