package masquerade.sim.channel.jms;

import java.util.logging.Logger;

import javax.jms.ConnectionFactory;

import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.JmsChannel;

import org.springframework.jms.listener.SimpleMessageListenerContainer;

public class JmsChannelListener extends AbstractChannelListener<JmsChannel> {

	private final static Logger log = Logger.getLogger(JmsChannelListener.class.getName());

	private String url;
	private String user;
	private String destinationName;
	private String password;
	private boolean isTopic;

	private SimpleMessageListenerContainer container;
	
	@Override
	public synchronized void onStart(JmsChannel channel) {
		url = channel.getUrl();
		user = channel.getUser();
		password = channel.getPassword();
		destinationName = channel.getDestinationName();
		isTopic = channel.isTopic();
		log.info("Starting JmsChannelListener at " + url + " with user " + user);
		
		ConnectionFactory connectionFactory = createConnectionFactory();
		
		container = new SimpleMessageListenerContainer();
		container.setPubSubDomain(isTopic);
		container.setConnectionFactory(connectionFactory);
		container.setDestinationName(destinationName);
		container.setMessageListener(this);
		container.setAutoStartup(true);
		container.initialize();
		container.start();
	}
	
	private ConnectionFactory createConnectionFactory() {
		// TODO: implement factory for connection factories, with implementations
		// for IBM MQ, Tibco EM, ActiveMQ, RabbitMQ
		return null;
	}

	@Override
	public synchronized void onStop() {
		log.info("Stopping JmsChannelListener");
		if (container != null) {
			container.stop();
			container = null;
		}
	}
}
