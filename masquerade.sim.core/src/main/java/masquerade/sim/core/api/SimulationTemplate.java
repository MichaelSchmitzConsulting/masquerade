package masquerade.sim.core.api;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletInputStream;

import masquerade.sim.model.Simulation;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;

/**
 * Common logic related to {@link Simulation} manipulation by API services.
 */
public class SimulationTemplate {
	private final ModelRepository modelRepository;
	private final Importer importer;

	public SimulationTemplate(ModelRepository modelRepository, Importer importer) {
		this.modelRepository = modelRepository;
		this.importer = importer;
	}

	public void insertSimulation(ServletInputStream inputStream, String[] channelIds) {
		Simulation simulation = importer.insertSimulation(inputStream);
		if (channelIds != null) {
			assignSimulationToChannel(simulation.getId(), Arrays.asList(channelIds));
		}
	}

	private void assignSimulationToChannel(String simulationId, List<String> channelIds) {
		for (String channelId : channelIds) {
			modelRepository.assignSimulationToChannel(simulationId, channelId);			
		}
	}
}
