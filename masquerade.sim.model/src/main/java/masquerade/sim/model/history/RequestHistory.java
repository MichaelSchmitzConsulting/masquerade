package masquerade.sim.model.history;

import java.util.Date;
import java.util.List;

/**
 * Interface for a request history service providing logging and 
 * access for request/response history. 
 */
public interface RequestHistory {
	HistoryEntry logRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo, String requestId, String requestData);
	
	void logNoMatch(Date requestTimestamp, Date receiveTimestamp, String channelName, String clientInfo, String convert);
	
	void setSuccess(String responseData, long processingPeriod, HistoryEntry entry);
	
	List<HistoryEntry> getLatestRequests(int maxAmount);
	
	/**
	 * Reads a specific request from the request history log.
	 * 
	 * @param requestId ID of the request to read
	 * @return {@link HistoryEntry} for this request, or <code>null</code> if not found
	 */
	HistoryEntry getRequest(String requestId);
	
	/**
	 * Reads a list of request from the request history log.
	 * 
	 * @param requestIdPrefix Prefix matched against the ID of the request
	 * @return List of {@link HistoryEntry} matching this request ID prefix
	 */
	List<HistoryEntry> getRequestsForIdPrefix(String requestIdPrefix);

	void clear();

	void cleanOldRequests(int requestsToKeep);

}
