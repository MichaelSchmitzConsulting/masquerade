package masquerade.sim.status;

import java.util.List;

import masquerade.sim.status.Status.Severity;

/**
 * A repository for {@link Status} log messages
 */
public interface StatusRepository {

	List<Status> latestStatusLogs();

	void addStatus(String name, String msg, Severity severity);

	void addStatus(String name, String msg, Throwable t, Severity severity);

}