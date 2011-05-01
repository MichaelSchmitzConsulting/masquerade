package masquerade.sim.db;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;
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
	
	private RequestHistoryFactory dbSessionFactory;
	private long cleanupSleepPeriod;
	private int requestsToKeep;
	
	private Thread thread;
	
	/**
	 * @param dbSessionFactory Request history DB session factory
	 */
	public RequestHistoryCleanupJob(RequestHistoryFactory dbSessionFactory, long cleanupSleepPeriod, int requestsToKeep) {
		this.dbSessionFactory = dbSessionFactory;
		this.cleanupSleepPeriod = Math.max(MIN_SLEEP_PERIOD, cleanupSleepPeriod);
		this.requestsToKeep = requestsToKeep;
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
				RequestHistory session = dbSessionFactory.startRequestHistorySession();
				try {
					doCleanup(session);
				} finally {
					session.endSession();
				}
				
				Thread.sleep(cleanupSleepPeriod);
			}
		}

		private void doCleanup(RequestHistory session) {
			session.cleanOldRequests(requestsToKeep);
		}
	}
}
