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
	 * @return All {@link Channel channels} contained in this repository
	 */
	Collection<Channel> getChannels();
	
	/**
	 * @param id Channel ID
	 * @return Channel with this id, or <code>null</code> if not found
	 */
	Channel getChannelById(String id);
	
	/**
	 * @param id Simulation ID
	 * @return The simulation with this id, or <code>null</code> if not found
	 */
	Simulation getSimulation(String id);
	
	/**
	 * Assign a simulation to be active on a channel
	 * @param simulationId
	 * @param channelId
	 */
	void assignSimulationToChannel(String simulationId, String channelId);
	
	/**
	 * @return The {@link Settings} instance contained in this repository
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
	 * Add/replace a channel, depending on whether a channel with 
	 * the same ID exists or not. 
	 * @param channel
	 */
	void insertChannel(Channel channel);

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
	 */
	void insertSimulation(Simulation simulation);

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
	Collection<Simulation> getSimulations();
	
}