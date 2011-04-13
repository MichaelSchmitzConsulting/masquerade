package masquerade.sim.history;

public interface RequestHistoryFactory {
	/**
	 * Returns a {@link RequestHistory} instance. Call {@link RequestHistory#endSession()} when done!
	 */
	RequestHistory startRequestHistorySession();
}
