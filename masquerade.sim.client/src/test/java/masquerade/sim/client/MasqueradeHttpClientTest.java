package masquerade.sim.client;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.impl.step.RenameXmlNodeStep;

import org.junit.Test;

public class MasqueradeHttpClientTest {

	private HttpService httpService = createStrictMock(HttpService.class);
	private MasqueradeHttpClient client = new MasqueradeHttpClient(httpService);
	
	/**
	 * Test method for {@link masquerade.sim.client.MasqueradeHttpClient#listRequests(java.lang.String)}.
	 */
	@Test
	public void testListRequests() {
		String firstPart = "abc\n" + "<bla/>";
		String secondPart = "def";
		String str = "------=_Part_0_227383376.1318946202703\n" + 
				"X-Masquerade-RequestId: requestId-123\n" + 
				"X-Masquerade-RequestTimestamp: 1970-01-01T01:00:00.000\n" + 
				"X-Masquerade-RequestTime: 0\n" + 
				"X-Masquerade-ReceiveTime: 0\n" + 
				"X-Masquerade-ProcessingPeriod: 123\n" + 
				"X-Masquerade-ChannelName: channelName-foo\n" + 
				"X-Masquerade-ClientInfo: clientInfo-abc\n" + 
				"\n" + 
				firstPart +
				"\n" + 
				"------=_Part_0_227383376.1318946202703\n" + 
				"X-Masquerade-RequestId: requestId-456\n" + 
				"X-Masquerade-RequestTimestamp: 1970-01-01T01:00:00.000\n" + 
				"X-Masquerade-RequestTime: 0\n" + 
				"X-Masquerade-ReceiveTime: 0\n" + 
				"X-Masquerade-ProcessingPeriod: 123\n" + 
				"X-Masquerade-ChannelName: channelName-foo\n" + 
				"X-Masquerade-ClientInfo: clientInfo-abc\n" + 
				"\n" +
				secondPart +
				"\n" + 
				"------=_Part_0_227383376.1318946202703--\n" + 
				"\n";
		InputStream content = new ByteArrayInputStream(str.getBytes());
		
		expect(httpService.getStream("api/listRequests/123")).andReturn(content);
		replay(httpService);
		
		List<Request> requests = client.listRequests("123");
		assertEquals(2, requests.size());
		
		assertEquals(firstPart, requests.get(0).getContent());
		assertEquals("requestId-123", requests.get(0).getRequestId());
		assertEquals("def", requests.get(1).getContent());
		assertEquals("requestId-456", requests.get(1).getRequestId());
		
		verify(httpService);
	}

	/**
	 * Test method for {@link masquerade.sim.client.MasqueradeHttpClient#dynamicResponseScript(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testProvideResponseScript() {
		String content = "<steps id=\"1\">\n" + 
				"  <masquerade.sim.model.CopyRequestToResponseStep id=\"2\">\n" + 
				"    <name>copy</name>\n" + 
				"  </masquerade.sim.model.CopyRequestToResponseStep>\n" + 
				"  <masquerade.sim.model.impl.step.RenameXmlNodeStep id=\"3\">\n" + 
				"    <name>rename</name>\n" + 
				"    <substituteVariables>true</substituteVariables>\n" + 
				"    <namespaceURI>http://example.com/ns</namespaceURI>\n" + 
				"    <newQualifiedName>ns:newName</newQualifiedName>\n" + 
				"    <selectNodeXpath>/</selectNodeXpath>\n" + 
				"  </masquerade.sim.model.impl.step.RenameXmlNodeStep>\n" + 
				"</steps>";
		
		httpService.post("api/dynamicResponseScript/123", content);
		replay(httpService);

		List<SimulationStep> steps = new LinkedList<SimulationStep>();
		steps.add(new CopyRequestToResponseStep("copy"));
		steps.add(new RenameXmlNodeStep("rename"));
		
		client.dynamicResponseScript("123", steps);
		
		verify(httpService);
	}

	/**
	 * Test method for {@link masquerade.sim.client.MasqueradeHttpClient#removeResponseScripts(java.lang.String)}.
	 */
	@Test
	public void testRemoveResponseScripts() {
		expect(httpService.get("api/removeResponseScripts/123")).andReturn("");
		replay(httpService);

		client.removeResponseScripts("123");
		
		verify(httpService);
	}
}
