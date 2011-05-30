package masquerade.sim.db;

import java.util.List;

import masquerade.sim.history.HistoryEntry;

public interface RequestHistoryStorage {

	void newEntry(HistoryEntry entry);

	void updateEntry(HistoryEntry entry);

	void clear();

	List<HistoryEntry> getLatestRequests(int maxAmount);

	HistoryEntry getRequestById(String requestId);

	void cleanOldRequests(int requestsToKeep);

	void endSession();

}
