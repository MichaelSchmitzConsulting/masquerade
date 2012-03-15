package masquerade.sim.model.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Simulation;

/**
 * Data transfer object holding all available objects
 * in a {@link ModelRepository}, for loading/saving from/to
 * persistent state using a {@link ModelPersistenceService},
 * or for import/export.  
 */
public class SimulationModel {
	public static final SimulationModel EMPTY = new SimulationModel();
	
	private final Collection<Channel> channels;
	private final Collection<Simulation> simulations;
	private final Map<String, Set<String>> channelToSimulations; 

	public SimulationModel(Collection<Channel> channels, Collection<Simulation> simulations, Map<String, Set<String>> channelToSimulations) {
		this.channels = new ArrayList<Channel>(channels);
		this.simulations = new ArrayList<Simulation>(simulations);
		this.channelToSimulations = new HashMap<String, Set<String>>();
		for (Map.Entry<String, Set<String>> entry : channelToSimulations.entrySet()) {
			Set<String> value = new LinkedHashSet<String>(entry.getValue());
			this.channelToSimulations.put(entry.getKey(), value);
		}
	}

	/** 
	 * Create initial, empty simulation model instance
	 */
	private SimulationModel() {
		this.channels = Collections.emptyList();
		this.simulations = Collections.emptyList();
		this.channelToSimulations = Collections.emptyMap();
	}
	
	public Collection<Channel> getChannels() {
		return channels;
	}

	public Collection<Simulation> getSimulations() {
		return simulations;
	}

	/**
	 * @return A {@link Map} containing the assignment of {@link Simulation}s to {@link Channel}s.
	 */
	public Map<String, Set<String>> getChannelToSimulations() {
		return channelToSimulations;
	}
}
