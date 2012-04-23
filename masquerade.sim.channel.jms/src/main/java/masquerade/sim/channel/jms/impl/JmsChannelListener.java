package masquerade.sim.channel.jms.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import masquerade.sim.channel.jms.JmsChannel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.ResponseCallback;
import masquerade.sim.model.ResponseDestination;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.DefaultResponseDestination;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

/**
 * A {@link ChannelListener} receiving requests on JMS queues or topics.
 * 
 * The JMS connection is created using an provider-specific {@link ConnectionFactoryProvider}. 
 */
public class JmsChannelListener extends AbstractChannelListener<JmsChannel> implements SessionAwareMessageListener<Message> {

	private final static StatusLog log = StatusLogger.get(JmsChannelListener.class);

	private String channelName;
	private String destinationName;
	private DefaultMessageListenerContainer container;
	private volatile String replyDestinationName; // Used in message handler threads
	private volatile boolean isTopic; // Used in message handler threads
	
	/**
	 * Start receiving requests from the topic/queue by creating a connection
	 * factory, and starting a message listener on it. 
	 */
	@Override
	public synchronized void onStart(JmsChannel channel, ChannelListenerContext context) {
		onStop(context);
		
		VariableHolder config = context.getVariableHolder();
		
		destinationName = config.substituteVariables(channel.getDestinationName());
		replyDestinationName = config.substituteVariables(channel.getResponseDestinationName());
		
		isTopic = channel.isTopic();
		channelName = channel.getId();
		log.info("Creating connection factory for JMS channel " + channelName);
		
		ConnectionFactory connectionFactory = createConnectionFactory(channel, context);
		if (connectionFactory == null) {
			// return, createConnectionFactory() logs errors itself
			return;
		}
		
		log.info("Starting JmsChannelListener " + channelName);
		container = new DefaultMessageListenerContainer();
		container.setBeanName("JMS-Channel-" + channel.getId());
		container.setPubSubDomain(isTopic);
		container.setConnectionFactory(connectionFactory);
		container.setDestinationName(destinationName);
		container.setMessageListener(this);
		container.setAutoStartup(true);
		container.setConcurrentConsumers(Math.max(1, channel.getConcurrentConsumers()));
		container.initialize();
		// Some JMS providers do not like session caching (e.g. ActiveMQ) - use connection caching
		container.setCacheLevel(DefaultMessageListenerContainer.CACHE_CONNECTION);
		container.start();
	}

	/**
	 * Stop the listener
	 */
	@Override
	public synchronized void onStop(ChannelListenerContext context) {
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

	private void onMessageInternal(final TextMessage txt, final Session session) throws JMSException, Exception {
		// Read request
		String text = txt.getText();
		final String correlationId = txt.getJMSCorrelationID();
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		long timestamp = txt.getJMSTimestamp();
		if (timestamp == 0) {
			timestamp = System.currentTimeMillis();
			log.trace("getJMSTimestamp() returns 0, using current system timestamp");
		}

		// Process request
		ResponseDestination responseDestination = createResponseDestination(txt, session, correlationId, responseOutput);
		processRequest("jms:" + destinationName, text, responseDestination, new Date(timestamp));
		
		// Send response
		if (responseOutput.size() > 0) {
			String response = new String(responseOutput.toByteArray());

			sendReply(correlationId, response, session, txt.getJMSReplyTo());
		}
	}

	private DefaultResponseDestination createResponseDestination(final TextMessage txt, final Session session, final String correlationId, ByteArrayOutputStream responseOutput) {
		return new DefaultResponseDestination(responseOutput) {
			@Override
			public void sendIntermediateResponse(ResponseCallback callback) throws Exception {
				ByteArrayOutputStream intermediateResponse = new ByteArrayOutputStream();
				callback.withResponse(intermediateResponse);
				String response = new String(intermediateResponse.toByteArray());
				log.trace("Sending intermediate response: " + response);
				sendReply(correlationId, response, session, txt.getJMSReplyTo());
			}
		};
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

	private ConnectionFactory createConnectionFactory(JmsChannel channel, ChannelListenerContext context) {
		ConnectionFactoryProvider provider;
		try {
			provider = channel.connectionFactoryProvider().newInstance();
			return provider.getConnectionFactory(channel, context);
		} catch (Throwable t) {
			log.error("Unable to create a connection factory for JMS channel " + channel.getId(), t);
			return null;
		}
	}
}
