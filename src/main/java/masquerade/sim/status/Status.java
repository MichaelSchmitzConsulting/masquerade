package masquerade.sim.status;

public class Status {
	public enum Severity {
		INFO, WARNING, ERROR
	};
	
	private String message;
	private String stacktrace;
	private Severity severity;
	private long timestamp;
	
	/**
	 * @param message
	 * @param stacktrace
	 * @param severity
	 * @param timestamp
	 */
	public Status(String message, String stacktrace, Severity severity, long timestamp) {
		this.message = message;
		this.stacktrace = stacktrace;
		this.severity = severity;
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * @return the stacktrace
	 */
	public String getStacktrace() {
		return stacktrace;
	}
	
	/**
	 * @return the severity
	 */
	public Severity getSeverity() {
		return severity;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
}
