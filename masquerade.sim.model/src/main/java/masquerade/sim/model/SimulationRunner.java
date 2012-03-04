package masquerade.sim.model;

import java.util.Date;

/**
 * Runs simulations if applicable to a request
 */
public interface SimulationRunner {
	void runSimulation(ResponseDestination responseDestination, String channelName, String clientInfo, Object request,
			Date requestTimestamp) throws Exception;
}
