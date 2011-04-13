package masquerade.sim;

import masquerade.sim.channel.ChannelListenerRegistry;
import masquerade.sim.db.DatabaseLifecycle;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.history.RequestHistoryFactory;

import com.db4o.ObjectContainer;

public class ApplicationContext {

	private DatabaseLifecycle databaseLifecycle;
	private ChannelListenerRegistry channelListenerRegistry;
	private RequestHistoryFactory requestHistoryFactory;
	
	public ApplicationContext(DatabaseLifecycle db, ChannelListenerRegistry channelListenerRegistry, RequestHistoryFactory requestHistoryFactory) {
		this.databaseLifecycle = db;
		this.channelListenerRegistry = channelListenerRegistry;
		this.requestHistoryFactory = requestHistoryFactory;
	}
	
	DatabaseLifecycle getDb() {
		return databaseLifecycle;
	}
	
	// TODO: Move to factory
	public ModelRepository startModelRepositorySession() {
		ObjectContainer db = databaseLifecycle.getDb();
		return new ModelRepository(db);
	}
	
	public RequestHistoryFactory getRequestHistoryFactory() {
		return requestHistoryFactory;
	}
	
	public ChannelListenerRegistry getChannelListenerRegistry() {
		return channelListenerRegistry;
	}	
}
