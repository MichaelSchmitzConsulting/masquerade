package masquerade.sim.core.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masquerade.sim.model.history.HistoryEntry;

public class InMemoryRequestHistoryStorage implements RequestHistoryStorage {

	private final Object lock = new Object();
	
	private List<HistoryEntry> store = new ArrayList<HistoryEntry>();
	private final Map<String, HistoryEntry> requestId2EntryMap = new HashMap<String, HistoryEntry>();
	
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
		clearSharedStorage();
	}

	private void clearSharedStorage() {
		List<HistoryEntry> toDelete;
		synchronized (lock) {
			toDelete = new ArrayList<HistoryEntry>(store);
			store.clear();
			requestId2EntryMap.clear();
		}

		// Expensive file delete operation is kept out of the locked section
		deleteFiles(toDelete);
	}

	private static void deleteFiles(List<HistoryEntry> toDelete) {
		for (HistoryEntry entry : toDelete) {
			entry.deleteLogFiles();
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
	public List<HistoryEntry> getRequestByIdPrefix(String requestIdPrefix) {
		synchronized (lock) {
			List<HistoryEntry> entries = new ArrayList<HistoryEntry>();
			for (Map.Entry<String, HistoryEntry> entry : requestId2EntryMap.entrySet()) {
				String id = entry.getKey();
				if (id.startsWith(requestIdPrefix)) {
					entries.add(entry.getValue());
				}
			}
			return entries;
		}
	}

	@Override
	public void cleanOldRequests(int requestsToKeep) {
		List<HistoryEntry> removalList = Collections.emptyList();
		synchronized (lock) {
			int size = store.size();
			if (requestsToKeep < size) {
				int amountToClear = size - requestsToKeep;
			
				// Remove entries to be deleted from the request ID map
				removalList = store.subList(0, amountToClear);
				for (HistoryEntry entry : removalList) {
					requestId2EntryMap.remove(entry.getRequestId());
				}
				
				// Set store to remaining entries, discarding old entries
				List<HistoryEntry> keepList = store.subList(amountToClear, size);
				store = new ArrayList<HistoryEntry>(keepList);
			}
		}
		
		deleteFiles(removalList);
	}

	public void onShutdown() {
		clearSharedStorage();
	}
}
