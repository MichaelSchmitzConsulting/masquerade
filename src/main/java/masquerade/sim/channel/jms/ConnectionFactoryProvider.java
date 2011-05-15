package masquerade.sim.channel.jms;

import java.lang.reflect.InvocationTargetException;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.JmsChannel;

/**
 * Interface for providers creating JMS {@link ConnectionFactory} objects for
 * specific JMS implementations.
 */
public interface ConnectionFactoryProvider {
	ConnectionFactory getConnectionFactory(JmsChannel channel) throws Exception;
}
