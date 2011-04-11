package masquerade.sim.history;

import java.util.List;

public interface RequestHistory {
	void logRequest(String channelName, String simulationName, String clientInfo, String requestId, String requestData);
	
	List<HistoryEntry> getLatestRequests(int maxAmount);
	
	void endSession();
}
