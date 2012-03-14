package masquerade.sim.model.impl;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;

import org.junit.Test;

public class DefaultSimulationTest {
	@Test
	public void testSerialization() {
		DefaultSimulation source = new DefaultSimulation("test", buildSelector(), buildIdProvider(), buildScript());
		DefaultSimulation target = new DefaultSimulation("test", buildSelector(), buildIdProvider(), buildScript());
		assertCanClone(source, target);
	}

	@Test
	public void testSerializationAlternativeProviderProvidedScript() {
		DefaultSimulation source = new DefaultSimulation("test", buildSelector(), buildAlternativesIdProvider(), buildProvidedResponseScript());
		DefaultSimulation target = new DefaultSimulation("test", buildSelector(), buildAlternativesIdProvider(), buildProvidedResponseScript());
		assertCanClone(source, target);
	}

	@Test
	public void testSerializationDynamicScript() {
		DefaultSimulation source = new DefaultSimulation("test", buildSelector(), buildAlternativesIdProvider(), buildDynamicScript());
		DefaultSimulation target = new DefaultSimulation("test", buildSelector(), buildAlternativesIdProvider(), buildDynamicScript());
		assertCanClone(source, target);
	}

	private static Script buildDynamicScript() {
		return new DynamicScript();
	}

	private static RequestIdProvider<?> buildAlternativesIdProvider() {
		return new XpathAlternativesRequestIdProvider();
	}

	private static Script buildProvidedResponseScript() {
		return new ProvidedResponse();
	}

	private static Script buildScript() {
		return new SequenceScript();
	}

	private static RequestIdProvider<?> buildIdProvider() {
		return new XPathRequestIdProvider();
	}

	private static RequestMapping<?> buildSelector() {
		return new XPathRequestMapping();
	}

}
