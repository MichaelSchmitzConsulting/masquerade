package masquerade.sim.model.repository;

import masquerade.sim.model.Simulation;

/**
 * Wraps a simulation entry in the {@link ModelRepository}, providing an {{@link #isPersistent()} property
 */
public interface SimulationWrapper {
	Simulation getSimulation();
	boolean isPersistent();
}
