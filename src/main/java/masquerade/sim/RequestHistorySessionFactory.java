package masquerade.sim;

import java.io.File;

import com.db4o.ObjectContainer;

import masquerade.sim.db.RequestHistoryImpl;
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
	public RequestHistory createRequestHistory() {
		ObjectContainer session = db.ext().openSession();
		return new RequestHistoryImpl(session, requestLogDir);
	}
}
