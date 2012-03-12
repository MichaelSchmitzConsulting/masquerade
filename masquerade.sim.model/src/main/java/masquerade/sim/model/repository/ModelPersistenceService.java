package masquerade.sim.model.repository;

import masquerade.sim.model.Settings;

/**
 * Service saving/loading model objects from/to persistent state.
 */
public interface ModelPersistenceService {
	void persistModel(SimulationModel model);
	/**
	 * @return Loaded model, or <code>null</code> if no model has been persisted before
	 */
	SimulationModel loadModel();

	void persistSettings(Settings settings);
	/**
	 * @return Loaded settings, or <code>null</code> if no settings have been persisted before
	 */
	Settings loadSettings();
}
