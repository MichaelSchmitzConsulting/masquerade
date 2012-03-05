package masquerade.sim.integrationtest.api;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import masquerade.sim.channel.http.HttpChannel;
import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.impl.SimulationImpl;
import masquerade.sim.model.impl.XPathRequestIdProvider;
import masquerade.sim.model.impl.XPathRequestMapping;
import masquerade.sim.util.DomUtil;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Integration test for uploading model objects to the simulator
 */
public class ModelUploadIT extends ClientBased {
	private static final String TEST_CHANNEL_ID = "1";
	private static final String TEST_CHANNEL_ID_2 = "2";
	private static final String TEST_CHANNEL_PATH = "test";
	private static final String TEST_CHANNEL_PATH_2 = "test2";
	private static final String CONTENT = "<test id=\"5\"/>";

	@Before
	public void setUp() {
		client().deleteAllChannels();
		client().deleteAllSimulations();
	}
	
	@Test
	public void testChannelUpload() {
		uploadTestChannel("testId", TEST_CHANNEL_PATH);
		
		// No exception (return code 200) when issuing a request to the created channel 
		// means channel has been started
		client().httpChannelRequest(TEST_CHANNEL_PATH, CONTENT);
	}

	private void uploadTestChannel(String id, String path) {
		HttpChannel channel = new HttpChannel(id);
		channel.setLocation(path);
		client().uploadChannel(channel);
	}

	@Test
	public void testSimulationUpload() {
		uploadTestChannel(TEST_CHANNEL_ID, TEST_CHANNEL_PATH);
		uploadTestChannel(TEST_CHANNEL_ID_2, TEST_CHANNEL_PATH_2);
		
		XPathRequestMapping selector = new XPathRequestMapping("testMapping");
		selector.setMatchXpath("/*");
		
		XPathRequestIdProvider requestIdProvider = new XPathRequestIdProvider("testId");
		requestIdProvider.setXpath("/*/@id");
		
		SequenceScript script = new SequenceScript("testScript");
		script.getSimulationSteps().add(new CopyRequestToResponseStep("step"));
		
		Simulation simulation = new SimulationImpl("testSim", selector, requestIdProvider, script);
		
		Set<String> channelIds = new HashSet<String>();
		channelIds.add(TEST_CHANNEL_ID);
		channelIds.add(TEST_CHANNEL_ID_2);
		client().uploadSimulation(simulation, channelIds);
		
		// Issue requests to test channels, verify if simulation is applied as expected
		assertResponseRootElementName(client().httpChannelRequest(TEST_CHANNEL_PATH, CONTENT), "test");
		assertResponseRootElementName(client().httpChannelRequest(TEST_CHANNEL_PATH_2, CONTENT), "test");
	}

	private static void assertResponseRootElementName(InputStream responseStream, String elementName) {
		Document doc = DomUtil.parse(responseStream);
		assertEquals(elementName, doc.getDocumentElement().getNodeName());
	}
}
