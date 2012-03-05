package masquerade.sim.core.importexport;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import masquerade.sim.core.repository.ModelRepositoryImpl;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelStub;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.SimulationStub;
import masquerade.sim.plugin.impl.PluginRegistryImpl;
import masquerade.sim.util.XStreamMarshallerFactory;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;

public class XmlImporterTest {

	private XmlImporter importer;
	private HierarchicalStreamWriter writer;
	private XStream xstream;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() {
		importer = new XmlImporter();
		importer.modelRepository = new ModelRepositoryImpl();
		importer.pluginRegistry = new PluginRegistryImpl();
		xstream = new XStreamMarshallerFactory().createXStream();
		outputStream = new ByteArrayOutputStream();
		writer = new BinaryStreamWriter(outputStream);
	}
	
	@Test
	public void testInsertChannel() {		
		Channel channel = new ChannelStub("test");
		xstream.marshal(channel, writer);
		
		importer.insertChannel(new ByteArrayInputStream(outputStream.toByteArray()));
		
		assertEquals(1, importer.modelRepository.getChannels().size());
	}

	@Test
	public void testInsertSimulation() {
		Simulation simulation = new SimulationStub("test");
		xstream.marshal(simulation, writer);
		
		importer.insertSimulation(new ByteArrayInputStream(outputStream.toByteArray()));
		
		assertEquals(1, importer.modelRepository.getSimulations().size());
	}

}
