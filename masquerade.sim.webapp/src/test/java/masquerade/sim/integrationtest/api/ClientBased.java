package masquerade.sim.integrationtest.api;

import masquerade.sim.client.MasqueradeClient;
import masquerade.sim.integrationtest.ClientFactory;
import masquerade.sim.integrationtest.IntegrationTestClientFactory;

/**
 * Base class for integration tests requiring access to a pre-configured
 * {@link MasqueradeClient} instance
 */
public abstract class ClientBased {

	private MasqueradeClient client;

	protected MasqueradeClient client() {
		if (client == null) {
			client = createClient();
		}
		return client;
	}
	
	private static MasqueradeClient createClient() {
		ClientFactory factory = new IntegrationTestClientFactory();
		return factory.createClient();
	}	
}
