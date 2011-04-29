package masquerade.sim.db;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.FileUtils;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

/**
 * Implementation of {@link RequestHistory} providing access to the
 * request history log.
 */
public class RequestHistoryImpl implements RequestHistory {

	private StatusLog log = StatusLogger.get(RequestHistoryImpl.class);
	
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
	public HistoryEntry logRequest(String channelName, String simulationName, String clientInfo, String requestId, String requestData) {
		if (isActive) {
			String fileName = saveRequestToFile(requestData);
			HistoryEntry entry = new HistoryEntry(new Date(), channelName, simulationName, clientInfo, requestId, fileName, requestLogDir.getAbsolutePath());
			dbSession.store(entry);
			dbSession.commit();
			return entry;
		} else {
			return null;
		}
	}
	
	@Override
	public void addResponse(String responseData, HistoryEntry entry) {
		File file = new File(requestLogDir, entry.getFileName() + HistoryEntry.RESPONSE_FILE_SUFFIX);
		try {
			FileUtils.writeStringToFile(file, responseData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write response data to file " + file.getAbsolutePath(), e);
		}
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
	public List<HistoryEntry> getLatestRequests(final int maxAmount) {
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
	public void cleanOldRequests(int requestsToKeep) {
		Query query = dbSession.query();
		query.constrain(HistoryEntry.class);
		query.sortBy(latestRequestsComparator);
		
		ObjectSet<HistoryEntry> objSet = query.execute();
		ArrayList<HistoryEntry> all = new ArrayList<HistoryEntry>(objSet);
		int size = all.size();
		int toDelete = size - requestsToKeep;
		if (toDelete > 0) {
			log.trace("Cleaning old request history entries (total = " + size + ", deleting = " + toDelete);
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
			log.trace("Request history size after cleanup: " + query.execute().size()); // TODO: Remove trace
		} else {
			log.trace("No need for request history cleanup, size = " + size);
		}
	}

	@Override
	public void endSession() {
		isActive = false;
	}

	private String saveRequestToFile(String requestData) {
		String fileName = UUID.randomUUID().toString();
		
		File file = new File(requestLogDir, fileName + HistoryEntry.REQUEST_FILE_SUFFIX);
		try {
			FileUtils.writeStringToFile(file, requestData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write request data to file", e);
		}
		
		return fileName;
	}
}
