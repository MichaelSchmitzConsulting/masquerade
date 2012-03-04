package masquerade.sim.core.api;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.plugin.impl.PluginRegistryImpl;
import masquerade.sim.util.XmlScriptMarshaller;

import org.easymock.Capture;
import org.junit.Test;

/**
 * Test cases for {@link DynamicResponseScriptService}
 */
public class DynamicResponseScriptServiceTest {

	private static final String REQUEST_ID = "123";

	@Test
	public void testProvideDynamicResponseScript() throws IOException {
		DynamicResponseScriptService service = new DynamicResponseScriptService();
		ResponseProvider responseProvider = createStrictMock(ResponseProvider.class);
		service.responseProvider = responseProvider;
		service.pluginRegistry = new PluginRegistryImpl();
		
		ArrayList<SimulationStep> steps = new ArrayList<SimulationStep>();
		steps.add(new CopyRequestToResponseStep("copy"));
		Capture<ArrayList<SimulationStep>> captured = new Capture<ArrayList<SimulationStep>>();
		responseProvider.provideResponseScript(eq(REQUEST_ID), capture(captured));
		replay(responseProvider);
		
		XmlScriptMarshaller marshaller = new XmlScriptMarshaller();
		String content = marshaller.marshal(steps);
		
		service.provideDynamicResponseScript(REQUEST_ID, new ByteArrayInputStream(content.getBytes()));
		
		verify(responseProvider);
		
		ArrayList<SimulationStep> capturedSteps = captured.getValue();
		assertEquals(1, capturedSteps.size());
		assertEquals("copy", capturedSteps.get(0).getName());
	}

}
