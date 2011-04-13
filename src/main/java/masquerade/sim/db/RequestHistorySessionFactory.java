package masquerade.sim.db;

import java.io.File;

import com.db4o.ObjectContainer;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;

public class RequestHistorySessionFactory implements RequestHistoryFactory {

	private ObjectContainer db;
	private File requestLogDir;

	public RequestHistorySessionFactory(ObjectContainer db, File requestLogDir) {
		this.db = db;
		this.requestLogDir = requestLogDir;
	}

	/**
	 * Returns a {@link RequestHistory} instance. Call {@link RequestHistory#endSession()} when done!
	 */
	@Override
	public RequestHistory startRequestHistorySession() {
		ObjectContainer session = db.ext().openSession();
		return new RequestHistoryImpl(session, requestLogDir);
	}
}
