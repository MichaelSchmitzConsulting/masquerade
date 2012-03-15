package masquerade.sim.model.importexport.impl;

import java.io.InputStream;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationModel;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.util.XStreamUnmarshallerFactory;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.thoughtworks.xstream.XStream;

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
	public Channel insertChannel(InputStream inputStream, boolean isPersistChannel) {
		Channel channel = (Channel) unmarshal(inputStream);
		modelRepository.insertChannel(channel, isPersistChannel);
		return channel;
	}

	@Override
	public Simulation insertSimulation(InputStream inputStream, boolean isPersistSimulation) {
		Simulation simulation = (Simulation) unmarshal(inputStream);
		modelRepository.insertSimulation(simulation, isPersistSimulation);
		return simulation;
	}

	private Object unmarshal(InputStream inputStream) {
		XStream xstream = new XStreamUnmarshallerFactory(pluginRegistry).createXStream();
		return xstream.fromXML(inputStream);
	}

	@Override
	public SimulationModel importModel(InputStream stream, boolean isReplaceExistingConfiguration) {
		SimulationModel model = (SimulationModel) unmarshal(stream);
		if (isReplaceExistingConfiguration) {
			 modelRepository.clear();
		}
		
		modelRepository.insertSimulationModel(model);
		return model;
	}
}
