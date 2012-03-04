package masquerade.sim.status.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import masquerade.sim.status.Status;
import masquerade.sim.status.StatusRepository;
import masquerade.sim.status.Status.Severity;
import masquerade.sim.util.StringUtil;

/**
 * Implementation of {@link StatusRepository}. Keeps {@link Status status log entries}
 * in-memory in a bounded list.
 */
public class StatusRepositoryImpl implements StatusRepository {
	private static final int MIN_STATUS_LOG_SIZE = 5;

	private int maxStatusCount = 100;
	private List<Status> statusList = new LinkedList<Status>();

	/**
	 * {@inheritDoc}
	 * Applies immediately, truncating the status log if necessary.
	 */
	@Override
	public void setHistorySize(int limit) {
		synchronized (statusList) {
			maxStatusCount = Math.max(MIN_STATUS_LOG_SIZE, limit);
			int size = statusList.size();
			if (size > maxStatusCount) {
				List<Status> subList = statusList.subList(size - maxStatusCount, size);
				statusList = new LinkedList<Status>(subList);
			}
		}
	}
	
	@Override
	public List<Status> latestStatusLogs() {
		synchronized (statusList) {			
			return new ArrayList<Status>(statusList);
		}
	}
	
	@Override
	public void clear() {
		synchronized (statusList) {			
			statusList.clear();
		}
	}

	public void addStatus(String name, String msg, Severity severity) {
		Status status = new Status(format(name, msg), null, severity, System.currentTimeMillis());
		addStatus(status);
	}

	public void addStatus(String name, String msg, Throwable t, Severity severity) {
		String stacktrace = StringUtil.strackTrace(t);
		Status status = new Status(format(name, msg), stacktrace, severity, System.currentTimeMillis());
		addStatus(status);
	}

	/**
	 * Adds a status log entry to the bounded list, truncating it 
	 * if its size grows to more than {@link #maxStatusCount}
	 * log entries.
	 * @param status
	 */
	private void addStatus(Status status) {
		synchronized (statusList) {
			if (statusList.size() >= maxStatusCount) {
				statusList.remove(0);
			}
			statusList.add(status);
		}
	}

	private static String format(String name, String msg) {
		return name + ": " + msg;
	}
}
