package masquerade.sim.model;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;

public interface SimulationRunner {
	public void runSimulation(OutputStream responseOutput, String channelName, String clientInfo, 
		Collection<RequestMapping<?>> requestMappings, Object request, Date requestTimestamp) throws Exception;
}
