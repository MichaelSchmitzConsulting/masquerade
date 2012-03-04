package masquerade.sim.core.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import masquerade.sim.model.history.HistoryEntry;

/**
 * Stub extension for {@link HistoryEntry}, allows to set a byte[] buffer for content
 * returned by {@link #readRequestData()} and returns constant values for all other properties.
 */
public class HistoryEntryStub extends HistoryEntry {
	public static final int PROCESSING_PERIOD = 123;
	public static final String REQUEST_ID = "requestId-123";
	public static final String CLIENT_INFO = "clientInfo-abc";
	public static final String SIMULATION_NAME = "simulationName-xyz";
	public static final String CHANNEL_NAME = "channelName-foo";
	public static final String TIMESTAMP = "1970-01-01T01:00:00.000";
	
	private byte[] content;
	
	public HistoryEntryStub(byte[] content) {
		super(new Date(0), new Date(0), CHANNEL_NAME, SIMULATION_NAME, CLIENT_INFO, REQUEST_ID, null, null);
		setSuccess(PROCESSING_PERIOD);
		this.content = content;
	}
	
	@Override
	public InputStream readRequestData() throws IOException {
		return new ByteArrayInputStream(content);
	}
}