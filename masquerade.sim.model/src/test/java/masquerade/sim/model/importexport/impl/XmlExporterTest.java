package masquerade.sim.model.importexport.impl;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelStub;
import masquerade.sim.model.importexport.Exporter;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.plugin.impl.PluginRegistryImpl;
import masquerade.sim.util.XStreamUnmarshallerFactory;

import org.junit.Test;

public class XmlExporterTest {

	@Test
	public void testExportModelObject() throws IOException {
		Exporter exporter = new XmlExporter();
		
		Channel channel = new ChannelStub("test");
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		exporter.exportModelObject(channel, stream);
		
		PluginRegistry pluginRegistry = new PluginRegistryImpl();
		XStreamUnmarshallerFactory factory = new XStreamUnmarshallerFactory(pluginRegistry);
		Channel result = (Channel) factory.createXStream().fromXML(stream.toString());
		assertEquals("test", result.getId());
	}

}
 