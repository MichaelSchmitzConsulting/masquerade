package masquerade.sim.channel.jms;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.JmsChannel;

/**
 * Interface for providers creating JMS {@link ConnectionFactory} objects for
 * specific JMS implementations.
 */
public interface ConnectionFactoryProvider {
	ConnectionFactory getConnectionFactory(JmsChannel channel);
}
