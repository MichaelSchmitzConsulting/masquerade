package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.JmsChannel;

import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * A {@link ChannelListener} receiving requests on JMS queues or topics.
 * 
 * The JMS connection is created using an provider-specific {@link ConnectionFactoryProvider}. 
 */
public class JmsChannelListener extends AbstractChannelListener<JmsChannel> implements SessionAwareMessageListener<Message> {

	private final static Logger log = Logger.getLogger(JmsChannelListener.class.getName());

	private volatile String replyDestinationName;
	private volatile boolean isTopic;
	private SimpleMessageListenerContainer container;
	
	/**
	 * Start receiving requests from the topic/queue by creating a connection
	 * factory, and starting a message listener on it. 
	 */
	@Override
	public synchronized void onStart(JmsChannel channel) {
		onStop();
		
		String destinationName = channel.getDestinationName();
		replyDestinationName = channel.getResponseDestinationName();
		isTopic = channel.isTopic();
		log.info("Starting JmsChannelListener at " + channel.getUrl());
		
		ConnectionFactory connectionFactory = createConnectionFactory(channel);
		
		container = new SimpleMessageListenerContainer();
		container.setPubSubDomain(isTopic);
		container.setConnectionFactory(connectionFactory);
		container.setDestinationName(destinationName);
		container.setMessageListener(this);
		container.setAutoStartup(true);
		container.initialize();
		container.start();
	}

	/**
	 * Stop the listener
	 */
	@Override
	public synchronized void onStop() {
		if (container != null) {
			log.info("Stopping JmsChannelListener");
			container.stop();
			container = null;
		}
	}
	
	@Override
	public void onMessage(Message msg, Session session) {
		if (msg instanceof TextMessage) {
			TextMessage txt = (TextMessage) msg;
			try {
				onMessageInternal(txt, session);
			} catch (Throwable e) {
				log.log(Level.SEVERE, "Exception while handling JMS message", e);
			}
		} else {
			log.log(Level.SEVERE, "JmsChannelListener can currently only handle TextMessage, received " + msg.getClass().getName());
		}
	}

	private void onMessageInternal(TextMessage txt, Session session) throws JMSException, Exception {
		// Read request
		String text = txt.getText();
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		processRequest("JMS Listener", text, responseOutput);
		
		// Send response
		if (responseOutput.size() > 0) {
			String correlationId = txt.getJMSCorrelationID();
			String response = new String(responseOutput.toByteArray());

			sendReply(correlationId, response, session);
		}
	}

	private void sendReply(String correlationId, String response, Session session) throws JMSException {
		TextMessage message = session.createTextMessage(response);
		
		// Set reply correlation ID
		if (isNotEmpty(correlationId)) {
			message.setJMSCorrelationID(correlationId);
		}
		
		// Create reply topic/queue
		Destination replyDestination;
		if (isTopic) {
			replyDestination = session.createTopic(replyDestinationName);
		} else {
			replyDestination = session.createQueue(replyDestinationName);
		}
		
		// Create producer
		MessageProducer producer = session.createProducer(replyDestination);
		
		// Send reply message
		producer.send(message);
	}

	private ConnectionFactory createConnectionFactory(JmsChannel channel) {
		ConnectionFactoryProvider provider = new ActiveMqConnectionFactoryProvider();
		return provider.getConnectionFactory(channel);
	}
}
