package masquerade.sim.model.repository.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelStub;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.impl.DefaultSimulation;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.impl.XPathRequestIdProvider;
import masquerade.sim.model.impl.XPathRequestMapping;

import org.junit.Test;

public class ModelBeanUtilsTest {

	private static final String ID = "id";

	@Test
	public void testCopySimulation() {
		XPathRequestMapping selector = new XPathRequestMapping();
		selector.setMatchXpath("/xpSel");
		
		XPathRequestIdProvider requestIdProvider = new XPathRequestIdProvider();
		requestIdProvider.setXpath("/xpId");
		
		SequenceScript script = new SequenceScript();
		script.setDescription("desc");
		
		Simulation simulation = new DefaultSimulation(ID, selector, requestIdProvider, script);
		
		Simulation copy = ModelBeanUtils.copySimulation(simulation);
		
		assertNotNull(copy);
		assertEquals(ID, copy.getId());
		
		assertNotSame(selector, copy.getSelector());
		assertEquals("/xpSel", ((XPathRequestMapping) copy.getSelector()).getMatchXpath());
		
		assertNotSame(requestIdProvider, copy.getRequestIdProvider());
		assertEquals("/xpId", ((XPathRequestIdProvider) copy.getRequestIdProvider()).getXpath());
		
		assertNotSame(script, copy.getScript());
		assertEquals("desc", ((SequenceScript) copy.getScript()).getDescription());
	}
	
	@Test
	public void testCopyChannel() throws Exception {
		Channel channel = new ChannelStub("test");
		Channel result = ModelBeanUtils.copyChannel(channel);
		
		assertEquals(channel, result);
		assertNotSame(channel, result);
	}

}
