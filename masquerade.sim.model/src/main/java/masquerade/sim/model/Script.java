package masquerade.sim.model;

/**
 * A simulation script definition
 */
public interface Script extends Named {
	@Override
	String getName();
	String getDescription();

	/**
	 * Run this script, returning a response object
	 * @param simulationContext
	 * @return A response object
	 * @throws Exception
	 */
	public Object run(SimulationContext simulationContext) throws Exception;
}
