package masquerade.sim.model.history;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class HistoryEntry {
	public static final String REQUEST_FILE_SUFFIX = ".request";
	public static final String RESPONSE_FILE_SUFFIX = ".response";
	
	public static final long NOT_SET = -1;
	
	private final long requestTimestamp;
	private final long receiveTimestamp;
	private final String channelName;
	private final String simulationName;
	private final String clientInfo;
	private final String requestId;
	private final String fileName;
	private final String requestLogDir;
	private long processingPeriod = NOT_SET;
	private String errorMessage = "";
	private RequestState state = RequestState.Pending; 
	
	public HistoryEntry(Date requestTimestamp, Date receiveTimestamp, String channelName, String simulationName, String clientInfo, String requestId, String fileName, String requestLogDir) {
		this.requestTimestamp = requestTimestamp.getTime();
		this.receiveTimestamp = receiveTimestamp.getTime();
		this.channelName = channelName;
		this.simulationName = simulationName;
		this.clientInfo = clientInfo;
		this.requestId = requestId;
		this.fileName = fileName;
		this.requestLogDir = requestLogDir;
	}
	
	public Date getRequestTimestamp() {
		return new Date(requestTimestamp);
	}
	public Date getReceiveTimestamp() {
		return new Date(receiveTimestamp);
	}
	public long getRequestTime() {
		return requestTimestamp;
	}
	public long getReceiveTime() {
		return receiveTimestamp;
	}
	/**
	 * @return How long it took to process the request (in ms), or {@link #NOT_SET} if not applicable
	 */
	public long getProcessingPeriod() {
		return processingPeriod;
	}
	/**
	 * @param processingPeriod How long it took to process the request (in ms)
	 */
	public void setSuccess(long processingPeriod) {
		this.processingPeriod = processingPeriod;
		this.state = RequestState.Success;
	}

	public void setError(String message) {
		state = RequestState.Error;
		errorMessage = message;
	}

	public void setNoMatch() {
		state = RequestState.NoMatch;
	}

	public String getChannelName() {
		return channelName;
	}
	public String getSimulationName() {
		return simulationName;
	}
	public String getClientInfo() {
		return clientInfo;
	}
	public String getRequestId() {
		return requestId;
	}
	public String getFileName() {
		File file = new File(fileName);
		return file.getName();
	}
	
	public RequestState getState() {
		return state;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public InputStream readRequestData() throws IOException {
		return FileUtils.openInputStream(new File(requestLogDir, fileName + REQUEST_FILE_SUFFIX));	
	}
	public InputStream readResponseData() throws IOException {
		return FileUtils.openInputStream(new File(requestLogDir, fileName + RESPONSE_FILE_SUFFIX));
	}

	public void deleteLogFiles() {
		File request = new File(requestLogDir, fileName + REQUEST_FILE_SUFFIX);
		File response = new File(requestLogDir, fileName + RESPONSE_FILE_SUFFIX);
		request.delete();
		response.delete();
	}
}
