package masquerade.sim.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Status {
	public enum Severity {
		INFO, WARNING, ERROR
	};
	
	private String message;
	private String stacktrace;
	private Severity severity;
	private String timestamp;
	
	private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
		@Override protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}		
	};
	
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
		this.timestamp = DATE_FORMAT.get().format(timestamp);
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
	public String getTimestamp() {
		return timestamp;
	}
}
