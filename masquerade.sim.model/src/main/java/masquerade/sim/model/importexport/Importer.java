package masquerade.sim.model.importexport;

import java.io.InputStream;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Simulation;

/**
 * Imports serialized model objects into the repository.
 */
public interface Importer {

	Channel insertChannel(InputStream inputStream);
	Simulation insertSimulation(InputStream inputStream);

}
