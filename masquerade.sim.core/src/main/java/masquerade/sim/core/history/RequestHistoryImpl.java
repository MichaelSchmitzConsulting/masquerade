package masquerade.sim.core.history;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;

import org.apache.commons.io.IOUtils;

/**
 * Implementation of {@link RequestHistory} providing access to the
 * request history log.
 */
public class RequestHistoryImpl implements RequestHistory {

	private final RequestHistoryStorage storage;
	private final File requestLogDir;
		
	public RequestHistoryImpl(RequestHistoryStorage storage, File requestLogDir) {
		this.storage = storage;
		this.requestLogDir = requestLogDir;
	}
	
	@Override
	public HistoryEntry logRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo, String requestId, String requestData) {
		return internalLogRequest(requestTimestamp, receiveTimestamp, channelName, simulationName, clientInfo, requestId, requestData, false);
	}

	private HistoryEntry internalLogRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo, String requestId, String requestData, boolean isNoMatch) {
		String fileName = saveRequestToFile(requestData);
		HistoryEntry entry = new HistoryEntry(requestTimestamp, receiveTimestamp, channelName, simulationName, clientInfo, requestId, fileName, requestLogDir.getAbsolutePath());
		
		if (isNoMatch) {
			entry.setNoMatch();
		}
		
		storage.newEntry(entry);
		return entry;
	}
	
	@Override
	public void logNoMatch(Date requestTimestamp, Date receiveTimestamp, String channelName, String clientInfo, String requestData) {
		internalLogRequest(requestTimestamp, receiveTimestamp, channelName, "<no match>", clientInfo, null, requestData, true);
	}

	@Override
	public void setSuccess(String responseData, long processingPeriodMs, HistoryEntry entry) {
		File file = new File(requestLogDir, entry.getFileName() + HistoryEntry.RESPONSE_FILE_SUFFIX);
		try {
			writeToFile(file, responseData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write response data to file " + file.getAbsolutePath(), e);
		}
		entry.setSuccess(processingPeriodMs);
		storage.updateEntry(entry);
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
	public List<HistoryEntry> getRequestsForIdPrefix(String requestIdPrefix) {
		if (requestIdPrefix == null || requestIdPrefix.length() == 0) {
			return null;
		}
		
		return storage.getRequestByIdPrefix(requestIdPrefix);
		
	}

	@Override
	public void cleanOldRequests(int requestsToKeep) {
		storage.cleanOldRequests(requestsToKeep);
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
