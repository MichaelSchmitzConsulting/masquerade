package masquerade.sim.db;

import java.io.File;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;

import com.db4o.ObjectContainer;

public class RequestHistorySessionFactory implements RequestHistoryFactory {

	private volatile boolean usePersistantStorage;
	private final ObjectContainer db;
	private final File requestLogDir;

	public RequestHistorySessionFactory(boolean usePersistantStorage, ObjectContainer db, File requestLogDir) {
		this.usePersistantStorage = usePersistantStorage;
		this.db = db;
		this.requestLogDir = requestLogDir;
	}

	public void setUsePersistantStorage(boolean usePersistantStorage) {
		this.usePersistantStorage = usePersistantStorage;
	}

	/**
	 * Returns a {@link RequestHistory} instance. Call {@link RequestHistory#endSession()} when done!
	 */
	@Override
	public RequestHistory startRequestHistorySession() {
		RequestHistoryStorage storage;
		if (usePersistantStorage) {
			ObjectContainer session = db.ext().openSession();
			storage = new PersistentRequestHistoryStorage(session);
		} else {
			storage = new InMemoryRequestHistoryStorage();
		}
		
		return new RequestHistoryImpl(storage, requestLogDir);
	}
}
