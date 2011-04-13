package masquerade.sim.model;

import masquerade.sim.history.RequestHistoryFactory;

public interface ChannelListener<T extends Channel> {
	
	void start(T channel, RequestHistoryFactory requestHistoryFactory);
	
	void stop();

}
