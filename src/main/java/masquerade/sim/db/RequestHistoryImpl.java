package masquerade.sim.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

public class RequestHistoryImpl implements RequestHistory {

	private volatile boolean isActive = true;
	private ObjectContainer dbSession;
	private File requestLogDir;
	
	private QueryComparator<HistoryEntry> latestRequestsComparator = new QueryComparator<HistoryEntry>() {
		@Override public int compare(HistoryEntry first, HistoryEntry second) {
			long diff = first.getTime() - second.getTime();
			return (int) diff;
		}
	};
	
	public RequestHistoryImpl(ObjectContainer db, File requestLogDir) {
		if (!requestLogDir.exists()) {
			try {
				FileUtils.forceMkdir(requestLogDir);
			} catch (IOException e) {
				throw new IllegalArgumentException("Cannot create log directory at " + requestLogDir.getName(), e);
			}
		}
		
		this.dbSession = db;
		this.requestLogDir = requestLogDir;
	}
	
	@Override
	public void logRequest(String channelName, String simulationName, String clientInfo, String requestId, String requestData) {
		if (isActive) {
			String fileName = saveRequestToFile(requestData);
			HistoryEntry entry = new HistoryEntry(new Date(), channelName, simulationName, clientInfo, requestId, fileName);
			dbSession.store(entry);
			dbSession.commit();
		}
	}
	
	@Override
	public void clear() {
		ObjectSet<HistoryEntry> result = dbSession.query(HistoryEntry.class);
		for (HistoryEntry entry : result) {
			dbSession.delete(entry);
		}
		dbSession.commit();
	}

	@Override
	public List<HistoryEntry> getLatestRequests(final int maxAmount) {
		Query query = dbSession.query();
		query.constrain(HistoryEntry.class);
		query.sortBy(latestRequestsComparator);
		
		ObjectSet<HistoryEntry> all = query.execute();
		List<HistoryEntry> ret = new ArrayList<HistoryEntry>(maxAmount);
		
		for (HistoryEntry historyEntry : all) {
			ret.add(historyEntry);
		}
		
		return ret;
	}

	@Override
	public InputStream getRequest(final String requestId) throws IOException {
		if (requestId == null || requestId.length() == 0) {
			return null;
		}
		
		ObjectSet<HistoryEntry> result = dbSession.query(new Predicate<HistoryEntry>() {
			@Override public boolean match(HistoryEntry entry) {
				return requestId.equals(entry.getRequestId());
			}
		});
		
		Iterator<HistoryEntry> it = result.iterator();
		return it.hasNext() ? it.next().readRequestData() : null;
	}

	@Override
	public void endSession() {
		isActive = false;
	}

	private String saveRequestToFile(String requestData) {
		String fileName = UUID.randomUUID().toString();
		
		File file = new File(requestLogDir, fileName);
		try {
			FileUtils.writeStringToFile(file, requestData);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot write request data to file", e);
		}
		
		return file.getAbsolutePath();
	}
}
