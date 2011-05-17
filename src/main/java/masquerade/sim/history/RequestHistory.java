package masquerade.sim.history;

import java.util.Date;
import java.util.List;

/**
 * Interface for a request history service providing logging and 
 * access for request/response history. 
 */
public interface RequestHistory {
	HistoryEntry logRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo, String requestId, String requestData);
	
	void addResponse(String responseData, long processingPeriod, HistoryEntry entry);
	
	List<HistoryEntry> getLatestRequests(int maxAmount);
	
	void endSession();

	/**
	 * Reads a specific request from the request history log.
	 * 
	 * @param requestId ID of the request to read
	 * @return {@link HistoryEntry} for this request, or {@link NullPointerException} if not found
	 */
	HistoryEntry getRequest(String requestId);

	void clear();

	void cleanOldRequests(int requestsToKeep);
}
