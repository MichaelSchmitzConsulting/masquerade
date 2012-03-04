package masquerade.sim.util;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.impl.step.RenameXmlNodeStep;
import masquerade.sim.plugin.impl.PluginRegistryImpl;

import org.junit.Test;

public class XmlScriptMarshallerTest {

	/**
	 * Test method for {@link masquerade.sim.util.XmlScriptMarshaller#marshal(masquerade.sim.model.impl.SequenceScript)}.
	 */
	@Test
	public void testMarshallingRoundtrip() {
		XmlScriptMarshaller marshaller = new XmlScriptMarshaller();
		XStreamUnmarshallerFactory factory = new XStreamUnmarshallerFactory(new PluginRegistryImpl());
		XmlScriptUnmarshaller unmarshaller = new XmlScriptUnmarshaller(factory);
		
		List<SimulationStep> steps = new LinkedList<SimulationStep>();
		steps.add(new CopyRequestToResponseStep("copy"));
		steps.add(new RenameXmlNodeStep("rename"));
		
		String content = marshaller.marshal(steps);
		
		List<SimulationStep> result = unmarshaller.unmarshal(content);
		
		assertEquals(2, result.size());
		assertEquals("copy", result.get(0).getName());
		assertEquals("rename", result.get(1).getName());
	}

}
