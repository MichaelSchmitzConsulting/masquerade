package masquerade.sim.model.repository;

import java.util.Collection;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Settings;
import masquerade.sim.model.Simulation;

/**
 * Repository for simulation configuration domain model 
 * objects
 */
public interface ModelRepository {

	/**
	 * @return All available channels
	 */
	Collection<ChannelWrapper> listChannels();
	
	/**
	 * @return All available channel IDs
	 */
	Collection<String> getAllChannelIds();
	
	/**
	 * @param id Channel ID
	 * @return Channel with this id, or <code>null</code> if not found
	 */
	Channel getChannel(String id);

	/**
	 * Returns a copy of the channel with the given Id, safe to be edited e.g. in the UI
	 * before being put back into the repository.
	 * @param id Channel ID
	 * @return Channel with this id, or <code>null</code> if not found
	 */
	Channel getChannelForUpdate(String id);
	
	/**
	 * Returns a copy of the simulation with the given id, the copy
	 * can be freely upated (e.g. in the UI) before being put back into
	 * the repository.
	 * 
	 * @param id Simulation ID
	 * @return The simulation with this id, or <code>null</code> if not found
	 */
	Simulation getSimulationForUpdate(String id);
	
	/**
	 * Assign a simulation to be active on a channel
	 * @param simulationId
	 * @param channelIds
	 */
	void assignSimulationToChannels(String simulationId, Collection<String> channelIds);
	
	/**
	 * @return A copy of the {@link Settings} contained in this repository
	 */
	Settings getSettings();

	/**
	 * Updates the settings object in the repository
	 * @param settings
	 */
	void updateSettings(Settings settings);

	/**
	 * @param channelId
	 * @return Simulations assigned to this channel
	 */
	Collection<Simulation> getSimulationsForChannel(String channelId);
	
	/**
	 * Lists all channel IDs the given simulation is assigned to
	 * @param simulationId
	 * @return List of channel IDs
	 */
	Collection<String> getChannelsForSimulation(String simulationId);
	
	/**
	 * Add/replace a channel, depending on whether a channel with 
	 * the same ID exists or not. 
	 * @param channel
	 * @param isPersistent Whether to persist this channel across restarts. Typically the case 
	 *                     with channels manually created in the UI, but not with channels
	 *                     uploaded by test cases.
	 */
	void insertChannel(Channel channel, boolean isPersistent);

	/**
	 * Delete a single channel
	 */
	boolean deleteChannel(String id);
	
	/**
	 * Delete all channels
	 */
	void deleteChannels();

	/**
	 * Add/replace a simulation, depending on wheter a simulation
	 * with the same ID already exists.
	 * @param simulation
	 * @param isPersistent Whether to persist this simulation across restarts. Typically the case 
	 *                     with simulations manually created in the UI, but not with simulations
	 *                     uploaded by test cases.
	 */
	void insertSimulation(Simulation simulation, boolean isPersistent);

	/**
	 * Add/replace a simulation, depending on wheter a simulation
	 * with the same ID already exists.
	 * @param simulation
	 * @param isPersistent Whether to persist this simulation across restarts. Typically the case 
	 *                     with simulations manually created in the UI, but not with simulations
	 *                     uploaded by test cases.
	 * @param channelIds Channels to assign this simulation to
	 */
	void insertSimulation(Simulation simulation, boolean isPersistent, Collection<String> channelIds);

	/**
	 * Delete a simulation
	 * @return <code>true</code> if a simulation with this id was deleted
	 */
	boolean deleteSimulation(String id);
	
	/**
	 * Delete all simulations
	 */
	void deleteSimulations();
	
	/**
	 * @return All available simulations
	 */
	Collection<SimulationWrapper> listSimulations();
}