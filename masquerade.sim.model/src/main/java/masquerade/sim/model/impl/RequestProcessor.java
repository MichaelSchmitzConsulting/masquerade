package masquerade.sim.model.impl;

import java.util.Date;

import masquerade.sim.model.ResponseDestination;

public interface RequestProcessor {
	void processRequest(String clientInfo, Object request, ResponseDestination responseDestination, Date requestTimestamp) throws Exception;
}
