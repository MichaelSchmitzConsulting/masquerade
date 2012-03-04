package masquerade.sim.client;

import java.util.Collections;
import java.util.List;

import masquerade.sim.model.CopyRequestToResponseStep;
import masquerade.sim.model.SimulationStep;

/**
 * Integration test assuming a local masqueade instance at port 8888
 */
public class ClientIntegrationTest {

	private static final String URL = "http://localhost:8888/masquerade";

	public static void main(String[] args) {
		MasqueradeClient client = new MasqueradeHttpClient(URL);

		List<SimulationStep> steps = 
			Collections.<SimulationStep>singletonList(new CopyRequestToResponseStep("copy"));
		client.dynamicResponseScript("456-789", steps);
		
		client.listRequests("bla");
		
		client.removeResponseScripts("456");
	}
}
