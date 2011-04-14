package masquerade.sim.model;

public interface ChannelListener<T extends Channel> {
	
	void start(T channel, SimulationRunner simulationRunner);
	
	void stop();
}
