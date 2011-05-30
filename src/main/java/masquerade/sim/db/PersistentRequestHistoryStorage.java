package masquerade.sim.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import masquerade.sim.history.HistoryEntry;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

public class PersistentRequestHistoryStorage implements RequestHistoryStorage {

	private static QueryComparator<HistoryEntry> latestRequestsComparator = new QueryComparator<HistoryEntry>() {
		@Override public int compare(HistoryEntry first, HistoryEntry second) {
			long diff = second.getRequestTime() - first.getRequestTime();
			return (int) diff;
		}
	};

	
	private ObjectContainer dbSession;

	public PersistentRequestHistoryStorage(ObjectContainer dbSession) {
		this.dbSession = dbSession;
	}
	
	@Override
	public void newEntry(HistoryEntry entry) {
		dbSession.store(entry);
		dbSession.commit();
	}

	@Override
	public void updateEntry(HistoryEntry entry) {
		dbSession.store(entry);
		dbSession.commit();
	}

	@Override
	public void clear() {
		ObjectSet<HistoryEntry> result = dbSession.query(HistoryEntry.class);
		for (HistoryEntry entry : result) {
			dbSession.delete(entry);
			entry.deleteLogFiles();
		}
		dbSession.commit();
	}

	@Override
	public List<HistoryEntry> getLatestRequests(int maxAmount) {
		Query query = dbSession.query();
		query.constrain(HistoryEntry.class);
		query.sortBy(latestRequestsComparator);
		
		ObjectSet<HistoryEntry> all = query.execute();
		List<HistoryEntry> ret = new ArrayList<HistoryEntry>(maxAmount);
		
		int curAmount = 0;
		for (HistoryEntry historyEntry : all) {
			if (curAmount++ > maxAmount) {
				break;
			}
			ret.add(historyEntry);
		}
		
		return ret;
	}

	@Override
	public HistoryEntry getRequestById(final String requestId) {
		ObjectSet<HistoryEntry> result = dbSession.query(new Predicate<HistoryEntry>() {
			@Override public boolean match(HistoryEntry entry) {
				return requestId.equals(entry.getRequestId());
			}
		});
		
		Iterator<HistoryEntry> it = result.iterator();
		return it.hasNext() ? it.next() : null;
	}

	@Override
	public void cleanOldRequests(int requestsToKeep) {
		Query query = dbSession.query();
		query.constrain(HistoryEntry.class);
		query.sortBy(latestRequestsComparator);
		
		ObjectSet<HistoryEntry> objSet = query.execute();
		ArrayList<HistoryEntry> all = new ArrayList<HistoryEntry>(objSet);
		int size = all.size();
		int toDelete = size - requestsToKeep;
		if (toDelete > 0) {
			List<HistoryEntry> removeEntries = all.subList(requestsToKeep, size);
			int i = 0;
			for (HistoryEntry entry : removeEntries) {
				dbSession.delete(entry);
				i++;
				if (i % 100 == 0) {
					dbSession.commit();
				}
			}
			dbSession.commit();
		}
	}

	@Override
	public void endSession() {
		dbSession.close();
	}

}
