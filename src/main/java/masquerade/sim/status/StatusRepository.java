package masquerade.sim.status;

import java.util.List;

/**
 * A repository for {@link Status} log messages
 */
public interface StatusRepository {

	/**
	 * @return Latest log messages kept in the status repository
	 */
	List<Status> latestStatusLogs();

	/**
	 * Delete all log messages
	 */
	void clear();


	/**
	 * Sets the status count history size. 
	 * 
	 * @param limit Max. amount of status log entries to keep
	 */
	void setHistorySize(int limit);
}