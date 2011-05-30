package masquerade.sim.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masquerade.sim.history.HistoryEntry;

public class InMemoryRequestHistoryStorage implements RequestHistoryStorage {

	private final static Object lock = new Object();
	
	private static List<HistoryEntry> store = new ArrayList<HistoryEntry>();
	private final static Map<String, HistoryEntry> requestId2EntryMap = new HashMap<String, HistoryEntry>();
	
	@Override
	public void newEntry(HistoryEntry entry) {
		synchronized (lock) {
			store.add(entry);
			String requestId = entry.getRequestId();
			if (requestId != null && !requestId.isEmpty()) {
				requestId2EntryMap.put(requestId, entry);
			}			
		}
	}

	@Override
	public void updateEntry(HistoryEntry entry) {
		// Do nothing - instance is updated already and always a live object
		// as there is no persistence.
	}

	@Override
	public void clear() {
		synchronized (lock) {
			store.clear();
			requestId2EntryMap.clear();
		}
	}

	@Override
	public List<HistoryEntry> getLatestRequests(int maxAmount) {
		synchronized (lock) {
			int end = store.size();
			int start = Math.max(0, end - maxAmount);
			List<HistoryEntry> subList = store.subList(start, end);
			return new ArrayList<HistoryEntry>(subList);
		}
	}

	@Override
	public HistoryEntry getRequestById(String requestId) {
		synchronized (lock) {
			return requestId2EntryMap.get(requestId);
		}
	}

	@Override
	public void cleanOldRequests(int requestsToKeep) {
		synchronized (lock) {
			int size = store.size();
			if (requestsToKeep < size) {
				int amountToClear = size - requestsToKeep;
			
				// Remove entries to be deleted from the request ID map
				List<HistoryEntry> removalList = store.subList(0, amountToClear);
				for (HistoryEntry entry : removalList) {
					requestId2EntryMap.remove(entry.getRequestId());
				}
				
				// Set store to remaining entries, discarding old entries
				List<HistoryEntry> keepList = store.subList(amountToClear, size);
				store = new ArrayList<HistoryEntry>(keepList);
			}
		}
	}

	@Override
	public void endSession() {
		// NOP
	}
}
