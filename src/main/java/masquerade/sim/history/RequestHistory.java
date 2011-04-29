package masquerade.sim.history;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for a request history service providing logging and 
 * access for request/response history. 
 */
public interface RequestHistory {
	HistoryEntry logRequest(String channelName, String simulationName, String clientInfo, String requestId, String requestData);
	
	void addResponse(String responseData, HistoryEntry entry);
	
	List<HistoryEntry> getLatestRequests(int maxAmount);
	
	void endSession();

	InputStream getRequest(String requestId) throws IOException;

	void clear();

	void cleanOldRequests(int requestsToKeep);
}
