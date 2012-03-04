package masquerade.sim.integrationtest;

import junit.framework.Assert;
import masquerade.sim.client.MasqueradeClient;
import masquerade.sim.client.MasqueradeHttpClient;

/**
 * Creates a {@link MasqueradeHttpClient} connecting to http://localhost:&lt;port&gt;,
 * where port is read from the system property <code>integration.test.server.port</code>.
 */
public class IntegrationTestClientFactory implements ClientFactory{

	private int port = -1;
	
	@Override
	public MasqueradeClient createClient() {
		return new MasqueradeHttpClient(serverUrl());
	}
	
	private String serverUrl() {
		return "http://localhost:" + port() + "/" + context();
	}
	
	protected String context() {
		String contextProperty = System.getProperty("integration.test.server.context");
		if (contextProperty == null) {
			return "";
		} else {
			return contextProperty;
		}
	}
	
	
	protected int port() {
		if (port > 0)
			return port;
		
		String portProperty = System.getProperty("integration.test.server.port");
		if (portProperty == null) {
			Assert.fail("System property integration.test.server.port not set - set to port of local webapp to use for integration tests");
		}
		
		return port = Integer.valueOf(portProperty);
	}
}
