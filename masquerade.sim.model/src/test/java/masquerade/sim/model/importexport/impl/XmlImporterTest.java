package masquerade.sim.model.importexport.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelStub;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.impl.DefaultSimulation;
import masquerade.sim.model.repository.ModelPersistenceService;
import masquerade.sim.model.repository.impl.ModelRepositoryImpl;
import masquerade.sim.plugin.impl.PluginRegistryImpl;
import masquerade.sim.util.XStreamMarshallerFactory;

import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import static org.easymock.EasyMock.*;

public class XmlImporterTest {

	private XmlImporter importer;
	private Writer writer;
	private XStream xstream;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() {
		ModelPersistenceService persistenceService = createStrictMock(ModelPersistenceService.class);
		replay(persistenceService);
		
		importer = new XmlImporter();
		importer.modelRepository = new ModelRepositoryImpl(persistenceService);
		importer.pluginRegistry = new PluginRegistryImpl();
		xstream = new XStreamMarshallerFactory().createXStream();
		outputStream = new ByteArrayOutputStream();
		writer = new OutputStreamWriter(outputStream);
	}
	
	@Test
	public void testInsertChannel() {		
		Channel channel = new ChannelStub("test");
		xstream.toXML(channel, writer);
		
		importer.insertChannel(new ByteArrayInputStream(outputStream.toByteArray()), false);
		
		assertEquals(1, importer.modelRepository.listChannels().size());
	}

	@Test
	public void testInsertSimulation() {
		Simulation simulation = new DefaultSimulation("test", null, null, null);
		xstream.toXML(simulation, writer);
		
		importer.insertSimulation(new ByteArrayInputStream(outputStream.toByteArray()), false);
		
		assertEquals(1, importer.modelRepository.listSimulations().size());
	}

}
