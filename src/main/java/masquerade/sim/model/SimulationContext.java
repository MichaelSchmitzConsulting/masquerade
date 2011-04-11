package masquerade.sim.model;

public interface SimulationContext {
	<R> R getContent(Class<R> expectedType);
	void setContent(Object content);
}
