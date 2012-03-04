package masquerade.sim.model.response;

import java.util.Date;
import java.util.List;

import masquerade.sim.model.SimulationStep;

/**
 * A response provider holds responses associated with specific
 * request IDs. It is used by clients to upload responses
 * for expected request IDs.
 */
public interface ResponseProvider {

	byte[] getResponse(String requestId);
	
	/**
	 * Setup the response for the specified request ID
	 * @param requestId
	 * @param response
	 */
	void provideResponse(String requestId, byte[] response);

	/**
	 * TODO: Cleanup job
	 */
	void cleanup(Date ifOlderThan);
	
	void provideResponseScript(String requestId, List<SimulationStep> steps);
	
	List<SimulationStep> getResponseScript(String requestId);
	
	/**
	 * @return Number of removed response scripts
	 */
	int removeResponseScripts(String requestIdPrefix);
}
