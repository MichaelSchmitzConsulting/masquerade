package masquerade.sim.status;

/**
 * Interface for status message (info, warning, error) logging
 * in masquerade. Provides access to the logs in the UI and
 * delegates to server logs.
 */
public interface StatusLog {
	void info(String msg);
	
	void warning(String msg);
	void warning(String msg, Throwable ex);
	
	void error(String msg);
	void error(String msg, Throwable ex);	
}
