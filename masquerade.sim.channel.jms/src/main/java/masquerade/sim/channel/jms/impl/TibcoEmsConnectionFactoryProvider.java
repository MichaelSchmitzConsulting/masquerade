package masquerade.sim.channel.jms.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.lang.reflect.Method;

import javax.jms.ConnectionFactory;

import masquerade.sim.channel.jms.DefaultJmsChannel;
import masquerade.sim.channel.jms.JmsChannel;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.VariableHolder;

/**
 * {@link ConnectionFactoryProvider} for Tibco EMS JMS 
 * queues/topics.
 */
public class TibcoEmsConnectionFactoryProvider implements ConnectionFactoryProvider {

	private static final String FACTORY_CLASS_NAME = "com.tibco.tibjms.TibjmsConnectionFactory";

	@Override
	public ConnectionFactory getConnectionFactory(JmsChannel channel, ChannelListenerContext context) throws Exception {
		DefaultJmsChannel jmsChannel = (DefaultJmsChannel) channel;
		
		VariableHolder config = context.getVariableHolder();
		
		// Read channel properties
		String serverUrl = config.substituteVariables(jmsChannel.getUrl());
		String user = config.substituteVariables(jmsChannel.getUser());
		String pwd = config.substituteVariables(jmsChannel.getPassword());
		
		// Get factory type and required methods
		Class<?> type = getClass().getClassLoader().loadClass(FACTORY_CLASS_NAME);
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
