package masquerade.sim.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.impl.ChannelListenerContextImpl;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * A registry for {@link ChannelListener} instances. Manages
 * lifecycle for these listeners.
 */
public class ChannelListenerRegistryImpl implements ChannelListenerRegistry {

	private static final StatusLog log = StatusLogger.get(ChannelListenerRegistryImpl.class);
	
	private final Map<String, ChannelListener<?>> channels = new LinkedHashMap<String, ChannelListener<?>>();
	private final SimulationRunner simulationRunner;
	private final ChannelListenerContext context;
	private final ModelRepository modelRepository;
	
	/**
	 * @param simulationRunner {@link SimulationRunner} passed to channel listeneres for handling requests
	 * @param variableHolder Configuration {@link VariableHolder} passed to channel listeners for environment-specific configuration
	 * @param classLoader 
	 */
	public ChannelListenerRegistryImpl(SimulationRunner simulationRunner, VariableHolder variableHolder, ModelRepository modelRepository,
			ClassLoader classLoader) {
		this.simulationRunner = simulationRunner;
		this.context = new ChannelListenerContextImpl(variableHolder, classLoader);
		this.modelRepository = modelRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends ChannelListener<?>> T getChannelListener(String name, Class<T> channelListenerType) {
		synchronized (channels) {
			@SuppressWarnings("unchecked")
			T listener = (T) channels.get(name);
			return listener;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T extends ChannelListener<?>> Collection<T> getAllListeners(Class<T> channelListenerTye) {
		Collection<T> ret = new ArrayList<T>();
		synchronized (channels) {
			for (ChannelListener<?> listener : channels.values()) {
				if (channelListenerTye.isAssignableFrom(listener.getClass())) {
					ret.add(channelListenerTye.cast(listener));
				}
			}
		}
		return ret;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startAll() {
		Collection<Channel> list = modelRepository.getChannels();
		
		synchronized (channels) {
			for (Channel channel : list) {
				try {
					updateChannel(channel);
				} catch (Exception ex) {
					log.error( "Exception while starting channel " + channel.getName(), ex);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopAll() {
		synchronized (channels) {
			Collection<ChannelListener<?>> listeners = channels.values();
			
			for (ChannelListener<?> listener : listeners) {
				listener.stop(context);
			}
			
			channels.clear();
		}
	}

	@Override
	public void startOrRestart(String name) {
		Channel updatedChannel = getChannel(name);
		updateChannel(updatedChannel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(String name) {
		synchronized (channels) {
			ChannelListener<?> listener = getChannelListener(name, ChannelListener.class);
			if (listener != null) {
				log.info("Stopping channel " + name);

				channels.remove(name);
				listener.stop(context);
			}
		}
	}	
	
	private void updateChannel(Channel changedChannel) {
		synchronized (channels) {
			@SuppressWarnings("unchecked")
			ChannelListener<Channel> listener = getChannelListener(changedChannel.getName(), ChannelListener.class);
			
			if (listener == null) {
				startChannel(changedChannel);
			} else {
				restartChannel(listener, changedChannel);
			}
		}
	}
	
	private Channel getChannel(String id) {
		return modelRepository.getChannel(id);
	}

	private void startChannel(Channel changedChannel) {
		String channelName = changedChannel.getName();
		if (changedChannel.isActive()) {
			log.info("Starting channel " + channelName);
			doStart(changedChannel, channelName);
		} else {
			log.info("Skipping channel start for inactive channel " + channelName);
		}
	}

	private void doStart(Channel changedChannel, String channelName) {
		try {
			ChannelListener<Channel> listener = createListener(changedChannel.listenerType());
			listener.start(changedChannel, simulationRunner, context);
			channels.put(channelName, listener);
		} catch (Exception ex) {
			log.error( "Cannot start channel listener", ex);
		}
	}	
	
	private ChannelListener<Channel> createListener(Class<? extends ChannelListener<?>> listenerType) {
		try {
			@SuppressWarnings("unchecked")
			ChannelListener<Channel> listener = (ChannelListener<Channel>) listenerType.newInstance();
			return listener;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Cannot instantiate channel listener: " + listenerType.getName(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Cannot instantiate channel listener: " + listenerType.getName(), e);
		}
	}

	private void restartChannel(ChannelListener<Channel> listener, Channel changedChannel) {
		log.info("Restarting channel " + changedChannel);
		listener.stop(context);
		listener.start(changedChannel, simulationRunner, context);
	}
}
