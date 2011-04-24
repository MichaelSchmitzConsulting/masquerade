package masquerade.sim.status;

import java.util.List;

/**
 * A repository for {@link Status} log messages
 */
public interface StatusRepository {

	List<Status> latestStatusLogs();

}