package masquerade.sim.model.importexport;

import java.io.InputStream;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationModel;

/**
 * Imports serialized model objects into the repository.
 */
public interface Importer {

	Channel insertChannel(InputStream inputStream, boolean isPersistChannel);
	Simulation insertSimulation(InputStream inputStream, boolean isPersistSimulation);
	
	/**
	 * Imports a {@link SimulationModel} into a {@link ModelRepository}
	 * @param stream Stream to read serialized model from
	 * @param isReplaceExistingConfiguration If true, the existing configuration is cleared before importing
	 * @return The imported model
	 */
	SimulationModel importModel(InputStream stream, boolean isReplaceExistingConfiguration);

}
