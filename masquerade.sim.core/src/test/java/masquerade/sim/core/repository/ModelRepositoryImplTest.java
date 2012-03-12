package masquerade.sim.core.repository;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelStub;
import masquerade.sim.model.Settings;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ChannelWrapper;
import masquerade.sim.model.repository.ModelPersistenceService;
import masquerade.sim.model.repository.SimulationWrapper;
import masquerade.sim.model.repository.impl.ModelRepositoryImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class ModelRepositoryImplTest {

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
		Channel channel1 = new ChannelStub("id1");
		Channel channel2 = new ChannelStub("id2");
		repo.insertChannel(channel1, false);
		repo.insertChannel(channel2, false);
		
		Collection<ChannelWrapper> wrappers = repo.listChannels();
		assertEquals(2, wrappers.size());
		
		Collection<Channel> channels = channels(wrappers);
		assertTrue(channels.contains(channel1));
		assertTrue(channels.contains(channel2));
		
		assertSame(channel1, repo.getChannel("id1"));
		assertSame(channel2, repo.getChannel("id2"));
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
		expect(simulation1.getId()).andReturn("id1").times(2);
		expect(simulation2.getId()).andReturn("id2");
		replay(simulation1, simulation2);
		
		repo.insertSimulation(simulation1, false);
		repo.insertSimulation(simulation2, false);
		repo.insertChannel(new ChannelStub("cid1"), false);
		repo.assignSimulationToChannel("id1", "cid1");
		repo.assignSimulationToChannel("id2", "cid1");
		
		Collection<SimulationWrapper> wrappers = repo.listSimulations();
		assertEquals(2, wrappers.size());
		
		Collection<Simulation> ids = new ArrayList<Simulation>();
		ids.add(simulation1);
		ids.add(simulation2);
		assertEquals(ids, repo.getSimulationsForChannel("cid1"));
		
		repo.deleteSimulation(simulation1.getId());

		wrappers = repo.listSimulations();
		assertEquals(1, wrappers.size());
		assertSame(simulation2, wrappers.iterator().next().getSimulation());
		
		assertEquals(Collections.singletonList(simulation2), repo.getSimulationsForChannel("cid1"));
		
		verify(simulation1, simulation2);
	}

	@Test
	public void testSettings() {
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
