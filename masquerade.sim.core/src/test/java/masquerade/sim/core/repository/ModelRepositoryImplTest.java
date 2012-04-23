package masquerade.sim.core.repository;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelStub;
import masquerade.sim.model.Settings;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.SimulationStub;
import masquerade.sim.model.repository.ChannelWrapper;
import masquerade.sim.model.repository.ModelPersistenceService;
import masquerade.sim.model.repository.SimulationWrapper;
import masquerade.sim.model.repository.impl.ModelRepositoryImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ModelRepositoryImpl}
 */
public class ModelRepositoryImplTest {

	private static final String SIM_ID_2 = "id2";
	private static final String SIM_ID_1 = "id1";
	private static final String CHANNEL_ID_1 = "cid1";
	private static final String PROPS = "xyz=abc";
	private ModelRepositoryImpl repo;
	private ModelPersistenceService persistence;

	@Before
	public void setUp() {
		persistence = createStrictMock(ModelPersistenceService.class);
		repo = new ModelRepositoryImpl(persistence);
	}
	
	@Test
	public void testChannels() {
		Channel channel1 = new ChannelStub(SIM_ID_1);
		Channel channel2 = new ChannelStub(SIM_ID_2);
		repo.insertChannel(channel1, false);
		repo.insertChannel(channel2, false);
		
		Collection<ChannelWrapper> wrappers = repo.listChannels();
		assertEquals(2, wrappers.size());
				
		Collection<Channel> channels = channels(wrappers);
		assertTrue(channels.contains(channel1));
		assertTrue(channels.contains(channel2));
		
		assertEquals(SIM_ID_1, repo.getChannel(SIM_ID_1).getId());
		assertEquals(SIM_ID_2, repo.getChannel(SIM_ID_2).getId());
		
		Collection<Simulation> sims = repo.getSimulationsForChannel(channel1.getId());
		assertTrue(sims.isEmpty());
		sims = repo.getSimulationsForChannel(channel2.getId());
		assertTrue(sims.isEmpty());

		Simulation simulation = new SimulationStub("sim1");
		repo.insertSimulation(simulation, false);
		repo.assignSimulationToChannels("sim1", Collections.singleton(SIM_ID_1));
		Collection<Simulation> simulationsForChannel = repo.getSimulationsForChannel(SIM_ID_1);
		assertEquals(1, simulationsForChannel.size());
		assertEquals("sim1", simulationsForChannel.iterator().next().getId());
	}

	private static Collection<Channel> channels(Collection<ChannelWrapper> wrappers) {
		Collection<Channel> channels = new ArrayList<Channel>();
		for (ChannelWrapper wrapper : wrappers) {
			channels.add(wrapper.getChannel());
		}
		return channels;
	}

	@Test
	public void testGetSimulation() {
		Simulation simulation1 = createStrictMock(Simulation.class);
		Simulation simulation2 = createStrictMock(Simulation.class);
		expect(simulation1.getId()).andReturn(SIM_ID_1).times(2);
		expect(simulation2.getId()).andReturn(SIM_ID_2).times(2);
		replay(simulation1, simulation2);
		
		repo.insertSimulation(simulation1, false);
		repo.insertSimulation(simulation2, false);
		repo.insertChannel(new ChannelStub(CHANNEL_ID_1), false);
		repo.assignSimulationToChannels(SIM_ID_1, Arrays.asList(CHANNEL_ID_1));
		repo.assignSimulationToChannels(SIM_ID_2, Arrays.asList(CHANNEL_ID_1));
		
		Collection<SimulationWrapper> wrappers = repo.listSimulations();
		assertEquals(2, wrappers.size());
		
		Collection<String> ids = Arrays.asList(SIM_ID_1, SIM_ID_2);
		Collection<String> storedIds = new ArrayList<String>();
		for (Simulation simulation : repo.getSimulationsForChannel(CHANNEL_ID_1)) {
			storedIds.add(simulation.getId());
		}
		assertEquals(ids, storedIds);
		
		repo.deleteSimulation(simulation1.getId());

		wrappers = repo.listSimulations();
		assertEquals(1, wrappers.size());
		assertEquals(simulation2.getId(), wrappers.iterator().next().getSimulation().getId());
		
		Collection<Simulation> simsOnChannel = repo.getSimulationsForChannel(CHANNEL_ID_1);
		assertEquals(1, simsOnChannel.size());
		assertEquals(SIM_ID_2, simsOnChannel.iterator().next().getId());
		
		verify(simulation1, simulation2);
	}

	@Test
	public void testSettings() {
		expect(persistence.loadSettings()).andReturn(null);

		Settings updatedSettings = new Settings();
		updatedSettings.setConfigurationProperties(PROPS);
		persistence.persistSettings(updatedSettings);
		replay(persistence);

		Settings settings = repo.getSettings();
		assertNotNull(settings);
		assertNotSame(settings, repo.getSettings()); // Settings should be cloned, not a live reference
		
		settings.setConfigurationProperties(PROPS);
		repo.updateSettings(settings);
		assertEquals(settings, repo.getSettings());
		
		verify(persistence);
	}

	@Test
	public void testDelete() {
		Channel channel = new ChannelStub("foo");
		repo.insertChannel(channel, false);
		
		Simulation simulation = createStrictMock(Simulation.class);
		expect(simulation.getId()).andReturn("bar");
		replay(simulation);
		repo.insertSimulation(simulation, false);
		
		repo.deleteChannels();
		assertTrue(repo.listChannels().isEmpty());
		assertFalse(repo.listSimulations().isEmpty());
		
		repo.deleteSimulations();
		assertTrue(repo.listSimulations().isEmpty());
		
		repo.insertChannel(channel, false);
		repo.deleteSimulations();
		assertFalse(repo.listChannels().isEmpty());
	}

}
