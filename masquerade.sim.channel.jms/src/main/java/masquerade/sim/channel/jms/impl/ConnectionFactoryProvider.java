package masquerade.sim.channel.jms.impl;

import javax.jms.ConnectionFactory;

import masquerade.sim.channel.jms.JmsChannel;
import masquerade.sim.model.ChannelListenerContext;

/**
 * Interface for providers creating JMS {@link ConnectionFactory} objects for
 * specific JMS implementations.
 */
public interface ConnectionFactoryProvider {
	ConnectionFactory getConnectionFactory(JmsChannel channel, ChannelListenerContext channelListenerContext) throws Exception;
}
