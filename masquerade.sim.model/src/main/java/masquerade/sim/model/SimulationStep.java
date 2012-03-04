package masquerade.sim.model;


/**
 * A single step in a simulation script
 */
public interface SimulationStep extends Named {
	@Override
	String getName();
	String getDescription();
	
	void execute(SimulationContext context) throws Exception;
}
