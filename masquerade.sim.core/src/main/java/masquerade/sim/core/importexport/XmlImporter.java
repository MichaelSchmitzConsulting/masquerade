package masquerade.sim.core.importexport;

import java.io.InputStream;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.util.XStreamUnmarshallerFactory;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;

/**
 * Imports simulation model objects serialized using XStream
 */
@Component
@Service
public class XmlImporter implements Importer {

	@Reference ModelRepository modelRepository;
	@Reference PluginRegistry pluginRegistry;
	@Reference ChannelListenerRegistry channelListenerRegistry;
	
	@Override
	public Channel insertChannel(InputStream inputStream) {
		Channel channel = (Channel) unmarshal(inputStream);
		modelRepository.insertChannel(channel);
		return channel;
	}

	@Override
	public Simulation insertSimulation(InputStream inputStream) {
		Simulation simulation = (Simulation) unmarshal(inputStream);
		modelRepository.insertSimulation(simulation);
		return simulation;
	}

	private Object unmarshal(InputStream inputStream) {
		XStream xstream = new XStreamUnmarshallerFactory(pluginRegistry).createXStream();
		return xstream.unmarshal(new BinaryStreamReader(inputStream));
	}
}
