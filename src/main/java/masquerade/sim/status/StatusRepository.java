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

}