package masquerade.sim.core.history;

import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * Cleanup job limiting the amount of logged requests
 * in the request history database.
 */
public class RequestHistoryCleanupJob {
	private static final StatusLog log = StatusLogger.get(RequestHistoryCleanupJob.class);

	private static final int SECOND = 1000;
	private static final long MIN_SLEEP_PERIOD = 5 * SECOND;
	private static final int MIN_REQUESTS_TO_KEEP = 5;

	private final RequestHistory requestHistory;
	private volatile long cleanupSleepPeriodMs;
	private volatile int requestsToKeep;
	
	private Thread thread;
	
	public RequestHistoryCleanupJob(RequestHistory requestHistory) {
		this.requestHistory = requestHistory;
	}

	public synchronized void start() {
		if (thread == null) {
			thread = new CleanupThread();
			thread.start();
		}
	}

	public synchronized void stop() {
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join(5000);
			} catch (InterruptedException e) {
				log.warning("Interrupted while waiting for request cleanup job to stop");
			}
			thread = null;
		}
	}
	
	/**
	 * The thread periodically doing the actual cleanup work
	 */
	private class CleanupThread extends Thread {
		@Override
		public void run() {
			try {
				internalRun();
			} catch (InterruptedException e) {
				// Thread stopped, return
			}
		}

		private void internalRun() throws InterruptedException {
			while (!isInterrupted()) {
				doCleanup();
				
				Thread.sleep(cleanupSleepPeriodMs);
			}
		}

		private void doCleanup() {
			requestHistory.cleanOldRequests(requestsToKeep);
		}
	}

	/**
	 * @param sleepPeriodMs Milliseconds to sleep between request history cleanup runs
	 */
	public void setSleepPeriodMs(long sleepPeriodMs) {
		cleanupSleepPeriodMs = Math.max(MIN_SLEEP_PERIOD, sleepPeriodMs);
	}

	/**
	 * @param requestsToKeep the amount of request to keep in the history
	 */
	public void setRequestsToKeep(int requestsToKeep) {
		this.requestsToKeep = Math.max(MIN_REQUESTS_TO_KEEP, requestsToKeep);
	}
}
