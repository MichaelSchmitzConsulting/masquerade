package masquerade.sim.model.impl;

import java.io.OutputStream;
import java.util.Date;

public interface RequestProcessor {
	void processRequest(String clientInfo,  Object request, OutputStream responseOutput, Date requestTimestamp) throws Exception;
}
