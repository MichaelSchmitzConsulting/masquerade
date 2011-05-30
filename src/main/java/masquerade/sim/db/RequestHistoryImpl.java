package masquerade.sim.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of {@link RequestHistory} providing access to the
 * request history log.
 */
public class RequestHistoryImpl implements RequestHistory {

	private static StatusLog log = StatusLogger.get(RequestHistoryImpl.class);

	private volatile boolean isActive = true;
	private RequestHistoryStorage storage;
	private File requestLogDir;
		
	public RequestHistoryImpl(RequestHistoryStorage storage, File requestLogDir) {
		this.storage = storage;
		this.requestLogDir = requestLogDir;
	}
	
	@Override
	public HistoryEntry logRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo,
			String requestId, String requestData) {
		if (isActive) {
			String fileName = saveRequestToFile(requestData);
			HistoryEntry entry = new HistoryEntry(requestTimestamp, receiveTimestamp, channelName, simulationName, clientInfo, requestId, fileName, requestLogDir.getAbsolutePath());
			storage.newEntry(entry);
			return entry;
		} else {
			return null;
		}
	}
	
	@Override
	public void addResponse(String responseData, long processingPeriodMs, HistoryEntry entry) {
		if (isActive) {
			File file = new File(requestLogDir, entry.getFileName() + HistoryEntry.RESPONSE_FILE_SUFFIX);
			try {
				writeToFile(file, responseData);
			} catch (IOException e) {
				throw new IllegalArgumentException("Cannot write response data to file " + file.getAbsolutePath(), e);
			}
			entry.setProcessingPeriod(processingPeriodMs);
			storage.updateEntry(entry);
		} else {
			log.error("addResponse called on closed request history object");
		}
	}

	@Override
	public void clear() {
		storage.clear();
	}

	@Override
	public List<HistoryEntry> getLatestRequests(int maxAmount) {
		return storage.getLatestRequests(maxAmount);
	}

	@Override
	public HistoryEntry getRequest(final String requestId) {
		if (requestId == null || requestId.length() == 0) {
			return null;
		}
		
		return storage.getRequestById(requestId);
	}

	@Override
	public void cleanOldRequests(int requestsToKeep) {
		storage.cleanOldRequests(requestsToKeep);
	}

	@Override
	public void endSession() {
		storage.endSession();
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
