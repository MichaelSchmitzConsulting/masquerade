package masquerade.sim.core.api;

import javax.servlet.ServletInputStream;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;

/**
 * Common logic related to {@link Channel} instance manipulation.
 */
public class ChannelTemplate {

	private final ChannelListenerRegistry channelListenerRegistry;

	public ChannelTemplate(ChannelListenerRegistry channelListenerRegistry) {
		this.channelListenerRegistry = channelListenerRegistry;
	}

	public void insert(Importer importer, ServletInputStream inputStream) {
		Channel channel = importer.insertChannel(inputStream);
		channelListenerRegistry.startOrRestart(channel.getName());
	}

	public void deleteChannel(ModelRepository modelRepository, String id) {
		modelRepository.deleteChannel(id);
		channelListenerRegistry.stop(id);
	}

	public void deleteChannels(ModelRepository modelRepository) {
		modelRepository.deleteChannels();
		channelListenerRegistry.stopAll();
	}
}
