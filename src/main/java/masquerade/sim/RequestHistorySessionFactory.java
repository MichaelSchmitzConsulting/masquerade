package masquerade.sim;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;

public class RequestHistorySessionFactory implements RequestHistoryFactory {
	
	private ApplicationContext applicationContext;

	public RequestHistorySessionFactory(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Returns a {@link RequestHistory} instance. Call {@link RequestHistory#endSession()} when done!
	 */
	@Override
	public RequestHistory getRequestHistory() {
		return applicationContext.startRequestHistorySession();
	}
}
