package masquerade.sim.core.history;

import java.util.List;

import masquerade.sim.model.history.HistoryEntry;

public interface RequestHistoryStorage {

	void newEntry(HistoryEntry entry);

	void updateEntry(HistoryEntry entry);

	void clear();

	List<HistoryEntry> getLatestRequests(int maxAmount);

	HistoryEntry getRequestById(String requestId);

	void cleanOldRequests(int requestsToKeep);

	List<HistoryEntry> getRequestByIdPrefix(String requestIdPrefix);

}
