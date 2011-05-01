package masquerade.sim.status;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import masquerade.sim.status.Status.Severity;

/**
 * Implementation of {@link StatusRepository}. Keeps {@link Status status log entries}
 * in-memory in a bounded list.
 */
public class StatusRepositoryImpl implements StatusRepository {
	private static final int MIN_STATUS_LOG_SIZE = 5;

	private static volatile int maxStatusCount = 100;
	
	private List<Status> statusList = new LinkedList<Status>();

	public static void setMaxStatusCount(int limit) {
		maxStatusCount = Math.max(MIN_STATUS_LOG_SIZE, limit);
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
		String stacktrace = strackTrace(t);
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

	/**
	 * @param t Throwable
	 * @return Stacktrace as a {@link String}
	 */
	private static String strackTrace(Throwable t) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		t.printStackTrace(printWriter);
		printWriter.flush();
		String stacktrace = stringWriter.toString();
		return stacktrace;
	}
}
