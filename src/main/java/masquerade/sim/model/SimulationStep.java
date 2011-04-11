package masquerade.sim.model;


/**
 * A single step in a simulation script
 */
public interface SimulationStep {
	String getName();
	String getDescription();
	
	void execute(SimulationContext context) throws Exception;
}
