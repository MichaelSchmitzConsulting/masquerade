package masquerade.sim.model.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Settings;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ModelRepository;

/**
 * Default implementation of {@link ModelRepository}, implements in-memory storage
 * of the simulation model and settings.
 */
public class ModelRepositoryImpl implements ModelRepository {

	private final Object lock = new Object();
	private final Object settingsLock = new Object();
	
	private final Map<String, Channel> channels = new HashMap<String, Channel>();
	private final Map<String, Simulation> simulations = new HashMap<String, Simulation>();
	private final Map<String, Set<String>> channelToSimulations = new HashMap<String, Set<String>>();
	private Settings settings;
	
	@Override
	public Collection<Channel> getChannels() {
		synchronized (lock) {
			return new ArrayList<Channel>(channels.values());
		}
	}

	@Override
	public Channel getChannel(String id) {
		synchronized (lock) {
			return channels.get(id);
		}
	}

	@Override
	public Channel getChannelForUpdate(String id) {
		Channel channel = getChannel(id);
		return ModelBeanUtils.copyChannel(channel); 
	}

	@Override
	public Simulation getSimulationForUpdate(String id) {
		synchronized (lock) {
			Simulation simulation = simulations.get(id);
			return ModelBeanUtils.copySimulation(simulation);
		}
	}

	@Override
	public void assignSimulationToChannel(String simulationId, String channelId) {
		synchronized (lock) {
			if (channels.containsKey(channelId)) {
				Set<String> sims = channelToSimulations.get(channelId);
				if (sims == null) {
					sims = new LinkedHashSet<String>();
					channelToSimulations.put(channelId, sims);
				}
				sims.add(simulationId);
			}
		}
	}

	@Override
	public Settings getSettings() {
		synchronized (settingsLock) {
			if (settings == null) {
				settings = new Settings();
			}
			return settings.clone();			
		}
	}

	@Override
	public void updateSettings(Settings settings) {
		synchronized (settingsLock) {
			this.settings = settings;
		}
	}

	@Override
	public Collection<Simulation> getSimulationsForChannel(String channelId) {
		Collection<Simulation> ret = null;
		synchronized (lock) {
			Set<String> simIds = channelToSimulations.get(channelId);
			if (simIds != null) {
				ret = new ArrayList<Simulation>();
				for (String simId : simIds) {
					Simulation simulation = simulations.get(simId);
					if (simulation != null) {
						ret.add(simulation);
					}
				}
			}
		}
		return ret == null ? Collections.<Simulation>emptySet() : ret;
	}

	@Override
	public void insertChannel(Channel channel) {
		synchronized (lock) {
			channels.put(channel.getId(), channel);
		}
	}

	@Override
	public void insertSimulation(Simulation simulation) {
		synchronized (lock) {
			simulations.put(simulation.getId(), simulation);
		}
	}
	
	@Override
	public Collection<Simulation> getSimulations() {
		synchronized (lock) {
			return new ArrayList<Simulation>(simulations.values());
		}
	}

	@Override
	public boolean deleteChannel(String id) {
		synchronized (lock) {
			channelToSimulations.remove(id);
			return channels.remove(id) != null;
		}
	}

	@Override
	public void deleteChannels() {
		synchronized (lock) {
			channels.clear();
			channelToSimulations.clear();
		}
	}

	/**
	 * Remove a simulation from the repository. Removes any assignments
	 * to channels for this simulation as well.
	 * @param id ID of the simulation to remove
	 */
	@Override
	public boolean deleteSimulation(String id) {
		synchronized (lock) {
			removeSimulationToChannelAssignment(id);
			return simulations.remove(id) != null;
		}
	}

	private void removeSimulationToChannelAssignment(String simulationId) {
		for (Entry<String, Set<String>> entry : channelToSimulations.entrySet()) {
			entry.getValue().remove(simulationId);
		}
	}

	@Override
	public void deleteSimulations() {
		synchronized (lock) {
			simulations.clear();
			channelToSimulations.clear();
		}
	}
}
