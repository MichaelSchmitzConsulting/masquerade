package masquerade.sim.history;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class HistoryEntry {
	public static final String REQUEST_FILE_SUFFIX = ".request";
	public static final String RESPONSE_FILE_SUFFIX = ".response";
	
	private long time;
	private String channelName;
	private String simulationName;
	private String clientInfo;
	private String requestId;
	private String fileName;
	private String requestLogDir;
	
	public HistoryEntry(Date timestamp, String channelName, String simulationName, String clientInfo, String requestId, String fileName, String requestLogDir) {
		this.time = timestamp.getTime();
		this.channelName = channelName;
		this.simulationName = simulationName;
		this.clientInfo = clientInfo;
		this.requestId = requestId;
		this.fileName = fileName;
		this.requestLogDir = requestLogDir;
	}
	
	public Date getTimestamp() {
		return new Date(time);
	}
	public long getTime() {
		return time;
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
