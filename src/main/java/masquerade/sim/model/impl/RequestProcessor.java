package masquerade.sim.model.impl;

import java.io.OutputStream;

public interface RequestProcessor {
	void processRequest(String clientInfo,  Object request, OutputStream responseOutput) throws Exception;
}
