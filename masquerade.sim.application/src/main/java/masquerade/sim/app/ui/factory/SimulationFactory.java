package masquerade.sim.app.ui.factory;

import masquerade.sim.model.Simulation;

/**
 * Interface for UI components allowing the user to create {@link Simulation}s
 */
public interface SimulationFactory {

	void createSimulation(SimulationFactoryCallback callback);

	interface SimulationFactoryCallback {
		void onCreate(Simulation simulation);
	}
}
