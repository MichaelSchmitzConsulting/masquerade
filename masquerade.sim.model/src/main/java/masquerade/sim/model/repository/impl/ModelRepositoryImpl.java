package masquerade.sim.model.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Settings;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ChannelWrapper;
import masquerade.sim.model.repository.ModelPersistenceService;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationModel;
import masquerade.sim.model.repository.SimulationWrapper;

/**
 * Default implementation of {@link ModelRepository}, implements in-memory storage
 * of the simulation model and settings.
 */
public class ModelRepositoryImpl implements ModelRepository {

	private final ModelPersistenceService persistenceService;
	
	private final Object lock = new Object();
	private final Object settingsLock = new Object();
	
	private final Map<String, ChannelWrapper> channels = new HashMap<String, ChannelWrapper>();
	private final Map<String, SimulationWrapper> simulations = new HashMap<String, SimulationWrapper>();
	private final Map<String, Set<String>> channelToSimulations = new HashMap<String, Set<String>>();
	private Settings settings;
	
	public ModelRepositoryImpl(ModelPersistenceService persistenceService) {
		this.persistenceService = persistenceService;
	}

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
	public void assignSimulationToChannels(String simulationId, Collection<String> channelIds) {
		synchronized (lock) {
			for (String channelId : channelIds) {
				if (channels.containsKey(channelId)) {
					Set<String> sims = channelToSimulations.get(channelId);
					sims.add(simulationId);
				}
			}
		}
	}

	@Override
	public Settings getSettings() {
		synchronized (settingsLock) {
			if (settings == null) {
				// Default settings if not yet persisted
				settings = new Settings();
			}
			return settings.clone();			
		}
	}

	@Override
	public void updateSettings(Settings settings) {
		synchronized (settingsLock) {
			this.settings = settings;
			persistenceService.persistSettings(settings);
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
			throw new IllegalArgumentException("Missing channel");
		}

		synchronized (lock) {
			ChannelWrapper wrapper = new ChannelWrapperImpl(channel, isPersistent);
			channels.put(channel.getId(), wrapper);
			channelToSimulations.put(channel.getId(), new LinkedHashSet<String>());
		
			if (isPersistent) {
				updatePersistentState();
			}
		}
	}

	private void updatePersistentState() {
		persistenceService.persistModel(createPersistentModel());
	}

	/** Callers must hold {@link #lock} */
	private SimulationModel createPersistentModel() {
		Collection<Channel> chans = new ArrayList<Channel>();
		for (ChannelWrapper wrapper : channels.values()) {
			if (wrapper.isPersistent()) {
				chans.add(wrapper.getChannel());
			}
		}
		
		Collection<Simulation> sims = new ArrayList<Simulation>();
		for (SimulationWrapper wrapper : simulations.values()) {
			if (wrapper.isPersistent()) {
				sims.add(wrapper.getSimulation());
			}
		}
		
		Map<String, Set<String>> persistentAssignments = new HashMap<String, Set<String>>();
		for (Map.Entry<String, Set<String>> assignment : channelToSimulations.entrySet()) {
			String channelId = assignment.getKey();
			if (channels.get(channelId).isPersistent()) {				
				Set<String> persistentSimIds = new LinkedHashSet<String>();
				Set<String> simulationIds = assignment.getValue();
				for (String simId : simulationIds) {
					if (simulations.get(simId).isPersistent()) {
						persistentSimIds.add(simId);
					}
				}
				
				persistentAssignments.put(channelId, persistentSimIds);				
			}
		}
		
		return new SimulationModel(chans, sims, persistentAssignments);
	}

	@Override
	public void insertSimulation(Simulation simulation, boolean isPersistent) {
		insertSimulation(simulation, isPersistent, Collections.<String>emptySet());
	}
	
	@Override
	public void insertSimulation(Simulation simulation, boolean isPersistent, Collection<String> assignToChannelIds) {
		if (simulation == null) {
			throw new IllegalArgumentException("Missing simulation");
		}
		
		synchronized (lock) {
			SimulationWrapper wrapper = new SimulationWrapperImpl(simulation, isPersistent);
			String simulationId = simulation.getId();
			simulations.put(simulationId, wrapper);
			assignSimulationToChannels(simulationId, assignToChannelIds);
			
			if (isPersistent) {
				updatePersistentState();
			}
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
			ChannelWrapper wrapper = channels.remove(id);
			if (wrapper != null && wrapper.isPersistent()) {
				updatePersistentState();
			}
			return wrapper != null;
		}
	}

	@Override
	public void deleteChannels() {
		synchronized (lock) {
			channels.clear();
			channelToSimulations.clear();
			updatePersistentState();
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
			SimulationWrapper wrapper = simulations.remove(id);
			if (wrapper != null && wrapper.isPersistent()) {
				updatePersistentState();
			}
			return wrapper != null;
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
			updatePersistentState();
		}
	}

	/**
	 * Loads model and settings from persistent state
	 */
	public void load() {
		synchronized (lock) {
			SimulationModel model = persistenceService.loadModel();
			
			if (model != null) {
				loadChannels(model);
				loadSimulations(model);
				loadSimulationAssignments(model);
			}
		}
		
		synchronized (settingsLock) {
			settings = persistenceService.loadSettings();
		}
	}

	private void loadSimulationAssignments(SimulationModel model) {
		channelToSimulations.clear();
		channelToSimulations.putAll(model.getChannelToSimulations());
	}

	private void loadChannels(SimulationModel model) {
		channels.clear();
		for (Channel channel : model.getChannels()) {
			channels.put(channel.getId(), new ChannelWrapperImpl(channel, true));				
		}
	}

	private void loadSimulations(SimulationModel model) {
		simulations.clear();
		for (Simulation simulation : model.getSimulations()) {
			simulations.put(simulation.getId(), new SimulationWrapperImpl(simulation, true));				
		}
	}

	@Override
	public Collection<String> getAllChannelIds() {
		Collection<String> channelIds = new HashSet<String>();
		synchronized (lock) {
			for (ChannelWrapper channel : channels.values()) {
				channelIds.add(channel.getChannel().getId());
			}
		}
		return channelIds;
	}

	@Override
	public Collection<String> getChannelsForSimulation(String simulationId) {
		Collection<String> channelIds = new HashSet<String>();
		synchronized (lock) {
			for (Map.Entry<String, Set<String>> entry : channelToSimulations.entrySet()) {
				Set<String> simulationIds = entry.getValue();
				if (simulationIds.contains(simulationId)) {
					String channelId = entry.getKey();
					channelIds.add(channelId);
				}
			}
		}
		return channelIds;
	}

	@Override
	public boolean containsSimulation(String id) {
		synchronized (lock) {
			return simulations.containsKey(id);
		}
	}

	@Override
	public boolean containsChannel(String id) {
		synchronized (lock) {
			return channels.containsKey(id);
		}
	}
}
