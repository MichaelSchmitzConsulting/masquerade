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
import masquerade.sim.model.repository.ChannelWrapper;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationWrapper;

/**
 * Default implementation of {@link ModelRepository}, implements in-memory storage
 * of the simulation model and settings.
 */
public class ModelRepositoryImpl implements ModelRepository {

	private final Object lock = new Object();
	private final Object settingsLock = new Object();
	
	private final Map<String, ChannelWrapper> channels = new HashMap<String, ChannelWrapper>();
	private final Map<String, SimulationWrapper> simulations = new HashMap<String, SimulationWrapper>();
	private final Map<String, Set<String>> channelToSimulations = new HashMap<String, Set<String>>();
	private Settings settings;
	
	@Override
	public Collection<ChannelWrapper> listChannels() {
		synchronized (lock) {
			return new ArrayList<ChannelWrapper>(channels.values());
		}
	}

	@Override
	public Channel getChannel(String id) {
		synchronized (lock) {
			ChannelWrapper wrapper = channels.get(id);
			return wrapper == null ? null : wrapper.getChannel();
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
			SimulationWrapper wrapper = simulations.get(id);
			return wrapper == null ? null : ModelBeanUtils.copySimulation(wrapper.getSimulation());
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
					SimulationWrapper wrapper = simulations.get(simId);
					if (wrapper != null) {
						ret.add(wrapper.getSimulation());
					}
				}
			}
		}
		return ret == null ? Collections.<Simulation>emptySet() : ret;
	}

	@Override
	public void insertChannel(Channel channel, boolean isPersistent) {
		if (channel == null) {
			return;
		}

		synchronized (lock) {
			ChannelWrapper wrapper = new ChannelWrapperImpl(channel, isPersistent);
			channels.put(channel.getId(), wrapper);
		}
	}

	@Override
	public void insertSimulation(Simulation simulation, boolean isPersistent) {
		if (simulation == null) {
			return;
		}
		
		synchronized (lock) {
			SimulationWrapper wrapper = new SimulatioWrapperImpl(simulation, isPersistent);
			simulations.put(simulation.getId(), wrapper);
		}
	}
	
	@Override
	public Collection<SimulationWrapper> listSimulations() {
		synchronized (lock) {
			return new ArrayList<SimulationWrapper>(simulations.values());
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
