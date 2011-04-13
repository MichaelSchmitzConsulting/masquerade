package masquerade.sim.history;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RequestHistory {
	void logRequest(String channelName, String simulationName, String clientInfo, String requestId, String requestData);
	
	List<HistoryEntry> getLatestRequests(int maxAmount);
	
	void endSession();

	InputStream getRequest(String requestId) throws IOException;

	void clear();
}
