package masquerade.sim;

import masquerade.sim.channel.ChannelListenerRegistry;
import masquerade.sim.db.DatabaseLifecycle;
import masquerade.sim.db.ModelRepositoryFactory;
import masquerade.sim.history.RequestHistoryFactory;

public class ApplicationContext {

	private DatabaseLifecycle databaseLifecycle;
	private ChannelListenerRegistry channelListenerRegistry;
	private RequestHistoryFactory requestHistoryFactory;
	private ModelRepositoryFactory modelRepositoryFactory;

	public ApplicationContext(DatabaseLifecycle db, ChannelListenerRegistry channelListenerRegistry, RequestHistoryFactory requestHistoryFactory,
			ModelRepositoryFactory modelRepositoryFactory) {
		this.databaseLifecycle = db;
		this.channelListenerRegistry = channelListenerRegistry;
		this.requestHistoryFactory = requestHistoryFactory;
		this.modelRepositoryFactory = modelRepositoryFactory;
	}

	DatabaseLifecycle getDb() {
		return databaseLifecycle;
	}

	public ModelRepositoryFactory getModelRepositoryFactory() {
		return modelRepositoryFactory;
	}

	public RequestHistoryFactory getRequestHistoryFactory() {
		return requestHistoryFactory;
	}

	public ChannelListenerRegistry getChannelListenerRegistry() {
		return channelListenerRegistry;
	}
}
