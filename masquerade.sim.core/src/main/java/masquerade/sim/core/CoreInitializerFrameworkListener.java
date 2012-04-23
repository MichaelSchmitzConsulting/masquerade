package masquerade.sim.core;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.repository.impl.ModelRepositoryImpl;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

/**
 * Handles startup tasks after the framework is fully initialized and all plugin
 * have finished registering.
 */
public class CoreInitializerFrameworkListener implements FrameworkListener {

	private final static StatusLog log = StatusLogger.get(CoreInitializerFrameworkListener.class);
	
	private final ModelRepositoryImpl modelRepository;
	private final ChannelListenerRegistry channelListenerRegistry;
	
	public CoreInitializerFrameworkListener(ModelRepositoryImpl modelRepository, ChannelListenerRegistry channelListenerRegistry) {
		this.modelRepository = modelRepository;
		this.channelListenerRegistry = channelListenerRegistry;
	}

	@Override
	public void frameworkEvent(FrameworkEvent event) {
		if (event.getType() == FrameworkEvent.STARTED) {
			log.info("Loading model repository from persistent state");
			modelRepository.load();
			
			log.info("Starting all available channels");
			channelListenerRegistry.startAll();
		}
	}

}
