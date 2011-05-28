package masquerade.sim.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;

import org.apache.commons.io.IOUtils;

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

	private volatile boolean isActive = true;
	private ObjectContainer dbSession;
	private File requestLogDir;
	
	private QueryComparator<HistoryEntry> latestRequestsComparator = new QueryComparator<HistoryEntry>() {
		@Override public int compare(HistoryEntry first, HistoryEntry second) {
			long diff = second.getRequestTime() - first.getRequestTime();
			return (int) diff;
		}
	};
	
	public RequestHistoryImpl(ObjectContainer db, File requestLogDir) {
		this.dbSession = db;
		this.requestLogDir = requestLogDir;
	}
	
	@Override
	public HistoryEntry logRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo,
			String requestId, String requestData) {
		if (isActive) {
			String fileName = saveRequestToFile(requestData);
			HistoryEntry entry = new HistoryEntry(requestTimestamp, receiveTimestamp, channelName, simulationName, clientInfo, requestId, fileName, requestLogDir.getAbsolutePath());
			dbSession.store(entry);
			dbSession.commit();
			return entry;
		} else {
			return null;
		}
	}
	
	@Override
	public void addResponse(String responseData, long processingPeriodMs, HistoryEntry entry) {
		File file = new File(requestLogDir, entry.getFileName() + HistoryEntry.RESPONSE_FILE_SUFFIX);
		try {
			writeToFile(file, responseData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write response data to file " + file.getAbsolutePath(), e);
		}
		entry.setProcessingPeriod(processingPeriodMs);
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
	public HistoryEntry getRequest(final String requestId) {
		if (requestId == null || requestId.length() == 0) {
			return null;
		}
		
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
		isActive = false;
	}

	private String saveRequestToFile(String requestData) {
		String fileName = UUID.randomUUID().toString();
		
		File file = new File(requestLogDir, fileName + HistoryEntry.REQUEST_FILE_SUFFIX);
		try {
			writeToFile(file, requestData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write request data to file", e);
		}
		
		return fileName;
	}

	private void writeToFile(File file, String requestData) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			IOUtils.write(requestData, out);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
