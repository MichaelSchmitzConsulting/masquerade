package masquerade.sim;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import masquerade.sim.db.DatabaseLifecycle;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.db.RequestHistoryImpl;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.model.Channel;

import com.db4o.ObjectContainer;

public class ApplicationContext {

	private DatabaseLifecycle databaseLifecycle;
	private File requestLogDir;
	private Collection<Channel> channels = new ArrayList<Channel>();
	
	public ApplicationContext(DatabaseLifecycle db, File requestLogDir) {
		this.databaseLifecycle = db;
		this.requestLogDir = requestLogDir;
	}
	
	public DatabaseLifecycle getDb() {
		return databaseLifecycle;
	}
	
	public ModelRepository startModelRepositorySession() {
		ObjectContainer db = openDbSession(true);
		return new ModelRepository(db);
	}
	
	public RequestHistory startRequestHistorySession() {
		ObjectContainer db = openDbSession(false);
		return new RequestHistoryImpl(db, requestLogDir);
	}
	
	private ObjectContainer openDbSession(boolean sharedGlobalSession) {
		if (sharedGlobalSession) {
			return databaseLifecycle.getDb();
		} else {
			return databaseLifecycle.getDb().ext().openSession();
		}
	}
	
	public void addChannel(Channel channel) {
		this.channels .add(channel);
	}
}
