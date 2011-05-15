package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.lang.reflect.Method;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.DefaultJmsChannel;
import masquerade.sim.model.impl.JmsChannel;

/**
 * {@link ConnectionFactoryProvider} for Tibco EMS JMS 
 * queues/topics.
 */
public class TibcoEmsConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final String FACTORY_CLASS_NAME = "com.tibco.tibjms.TibjmsConnectionFactory";

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel) throws Exception {
		DefaultJmsChannel jmsChannel = (DefaultJmsChannel) channel;
		
		// Read channel properties
		String serverUrl = jmsChannel.getUrl();
		String user = jmsChannel.getUser();
		String pwd = jmsChannel.getPassword();
		
		// Get factory type and required methods
		Class<?> type = Class.forName(FACTORY_CLASS_NAME);
		Method setServerUrlMethod = type.getMethod("setServerUrl", String.class);
		Method setUserNameMethod = type.getMethod("setUserName", String.class);
		Method setUserPasswordMethod = type.getMethod("setUserPassword", String.class);
		
		// Create factory instance
		ConnectionFactory factory = (ConnectionFactory) type.newInstance();
		
		// Invoke setters on factory
		setServerUrlMethod.invoke(factory, serverUrl);
		if (!isEmpty(user)) {
			setUserNameMethod.invoke(factory, user);
		}
		if (!isEmpty(pwd)) {
			setUserPasswordMethod.invoke(factory, pwd);
		}
		
		return factory;
	}
}
