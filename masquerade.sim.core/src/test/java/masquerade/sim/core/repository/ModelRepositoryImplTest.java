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
import masquerade.sim.model.repository.impl.ModelRepositoryImpl;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class ModelRepositoryImplTest {

	private ModelRepositoryImpl repo;

	@Before
	public void setUp() {
		repo = new ModelRepositoryImpl();
	}
	
	@Test
	public void testChannels() {
		Channel channel1 = new ChannelStub("id1");
		Channel channel2 = new ChannelStub("id2");
		repo.insertChannel(channel1);
		repo.insertChannel(channel2);
		
		Collection<Channel> channels = repo.getChannels();
		assertEquals(2, channels.size());
		assertTrue(channels.contains(channel1));
		assertTrue(channels.contains(channel2));
		
		assertSame(channel1, repo.getChannel("id1"));
		assertSame(channel2, repo.getChannel("id2"));
	}

	@Test
	public void testGetSimulation() {
		Simulation simulation1 = createStrictMock(Simulation.class);
		Simulation simulation2 = createStrictMock(Simulation.class);
		expect(simulation1.getId()).andReturn("id1").times(2);
		expect(simulation2.getId()).andReturn("id2");
		replay(simulation1, simulation2);
		
		repo.insertSimulation(simulation1);
		repo.insertSimulation(simulation2);
		repo.insertChannel(new ChannelStub("cid1"));
		repo.assignSimulationToChannel("id1", "cid1");
		repo.assignSimulationToChannel("id2", "cid1");
		
		Collection<Simulation> simulations = repo.getSimulations();
		assertEquals(2, simulations.size());
		
		Collection<Simulation> ids = new ArrayList<Simulation>();
		ids.add(simulation1);
		ids.add(simulation2);
		assertEquals(ids, repo.getSimulationsForChannel("cid1"));
		
		repo.deleteSimulation(simulation1.getId());

		simulations = repo.getSimulations();
		assertEquals(1, simulations.size());
		assertSame(simulation2, simulations.iterator().next());
		
		assertEquals(Collections.singletonList(simulation2), repo.getSimulationsForChannel("cid1"));
		
		verify(simulation1, simulation2);
	}

	@Test
	public void testSettings() {
		Settings settings = repo.getSettings();
		assertNotNull(settings);
		assertNotSame(settings, repo.getSettings()); // Settings should be cloned, not a live reference
		
		settings.setConfigurationProperties("xyz=abc");
		repo.updateSettings(settings);
		assertEquals(settings, repo.getSettings());
	}

	@Test
	public void testDelete() {
		Channel channel = new ChannelStub("foo");
		repo.insertChannel(channel);
		
		Simulation simulation = createStrictMock(Simulation.class);
		expect(simulation.getId()).andReturn("bar");
		replay(simulation);
		repo.insertSimulation(simulation);
		
		repo.deleteChannels();
		assertTrue(repo.getChannels().isEmpty());
		assertFalse(repo.getSimulations().isEmpty());
		
		repo.deleteSimulations();
		assertTrue(repo.getSimulations().isEmpty());
		
		repo.insertChannel(channel);
		repo.deleteSimulations();
		assertFalse(repo.getChannels().isEmpty());
	}

}
