package masquerade.sim.channel.jms;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.JmsChannel;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/**
 * A {@link ChannelListener} receiving requests on JMS queues or topics.
 * 
 * The JMS connection is created using an provider-specific {@link ConnectionFactoryProvider}. 
 */
public class JmsChannelListener extends AbstractChannelListener<JmsChannel> implements SessionAwareMessageListener<Message> {

	private final static StatusLog log = StatusLogger.get(JmsChannelListener.class);

	private String channelName;
	private String destinationName;
	private SimpleMessageListenerContainer container;
	private volatile String replyDestinationName; // Used in message handler threads
	private volatile boolean isTopic; // Used in message handler threads
	
	/**
	 * Start receiving requests from the topic/queue by creating a connection
	 * factory, and starting a message listener on it. 
	 */
	@Override
	public synchronized void onStart(JmsChannel channel) {
		onStop();
		
		VariableHolder config = getContext().getVariableHolder();
		
		destinationName = config.substituteVariables(channel.getDestinationName());
		replyDestinationName = config.substituteVariables(channel.getResponseDestinationName());
		
		isTopic = channel.isTopic();
		channelName = channel.getName();
		log.info("Creating connection factor for JMS channel " + channelName);
		
		ConnectionFactory connectionFactory = createConnectionFactory(channel);
		if (connectionFactory == null) {
			// return, createConnectionFactory() logs errors itself
			return;
		}
		
		log.info("Starting JmsChannelListener " + channelName);
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
				log.error("Exception while handling JMS message", e);
			}
		} else {
			log.error("JmsChannelListener can currently only handle TextMessage, received " + msg.getClass().getName());
		}
	}

	private void onMessageInternal(TextMessage txt, Session session) throws JMSException, Exception {
		// Read request
		String text = txt.getText();
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		processRequest("jms:" + destinationName, text, responseOutput);
		
		// Send response
		if (responseOutput.size() > 0) {
			String correlationId = txt.getJMSCorrelationID();
			String response = new String(responseOutput.toByteArray());

			sendReply(correlationId, response, session, txt.getJMSReplyTo());
		}
	}

	private void sendReply(String correlationId, String response, Session session, Destination messageReplyDestination) throws JMSException {
		TextMessage message = session.createTextMessage(response);
		
		// Set reply correlation ID
		if (isNotEmpty(correlationId)) {
			message.setJMSCorrelationID(correlationId);
		}
		
		// Create reply topic/queue
		Destination replyDestination;
		if (messageReplyDestination != null) {
			replyDestination = messageReplyDestination;
		} else {
			if (isTopic) {
				replyDestination = session.createTopic(replyDestinationName);
			} else {
				replyDestination = session.createQueue(replyDestinationName);
			}
		}
		
		MessageProducer producer = null;
		try {
			// Create producer
			producer = session.createProducer(replyDestination);
			
			// Send reply message
			producer.send(message);
		} finally {
			if (producer != null) {
				producer.close();
			}
		}
	}

	private ConnectionFactory createConnectionFactory(JmsChannel channel) {
		ConnectionFactoryProvider provider;
		try {
			provider = channel.connectionFactoryProvider().newInstance();
			return provider.getConnectionFactory(channel, getContext());
		} catch (Throwable t) {
			log.error("Unable to create a connection factory for JMS channel " + channel.getName(), t);
			return null;
		}
	}
}
