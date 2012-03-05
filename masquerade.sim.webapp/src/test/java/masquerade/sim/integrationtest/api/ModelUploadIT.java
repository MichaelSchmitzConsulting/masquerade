package masquerade.sim.integrationtest.api;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import masquerade.sim.channel.http.HttpChannel;
import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.impl.SimulationImpl;
import masquerade.sim.model.impl.XPathRequestIdProvider;
import masquerade.sim.model.impl.XPathRequestMapping;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for uploading model objects to the simulator
 */
public class ModelUploadIT extends ClientBased {
	private static final String CONTENT = "<test/>";

	@Before
	public void setUp() {
		client().deleteAllChannels();
		client().deleteAllSimulations();
	}
	
	@Test
	public void testChannelUpload() {
		uploadTestChannel();
		
		// No exception (return code 200) when issuing a request to the created channel 
		// means channel has been started
		client().httpChannelRequest("test", CONTENT);
	}

	private void uploadTestChannel() {
		HttpChannel channel = new HttpChannel("testChannel");
		channel.setLocation("test");
		client().uploadChannel(channel);
	}

	@Test
	public void testSimulationUpload() throws IOException {
		uploadTestChannel();
		
		XPathRequestMapping selector = new XPathRequestMapping("testMapping");
		selector.setMatchXpath("/*");
		
		RequestIdProvider<?> requestIdProvider = new XPathRequestIdProvider("testId");
		SequenceScript script = new SequenceScript("testScript");
		script.getSimulationSteps().add(new CopyRequestToResponseStep("step"));
		
		Simulation simulation = new SimulationImpl("testSim", selector, requestIdProvider, script);
		
		client().uploadSimulation(simulation, Collections.singleton("test"));
		
		InputStream resp = client().httpChannelRequest("test", CONTENT);
		assertEquals(CONTENT, IOUtils.toString(resp));
	}
}
