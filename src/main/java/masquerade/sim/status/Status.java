package masquerade.sim.status;

public class Status {
	public enum Severity {
		INFO, WARNING, ERROR
	};
	
	private String message;
	private String stacktrace;
	private Severity severity;
	
	/**
	 * @param message
	 * @param stacktrace
	 * @param severity
	 */
	Status(String message, String stacktrace, Severity severity) {
		this.message = message;
		this.stacktrace = stacktrace;
		this.severity = severity;
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
}
