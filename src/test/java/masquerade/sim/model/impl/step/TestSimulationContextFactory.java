package masquerade.sim.model.impl.step;

import masquerade.sim.converter.CompoundConverter;
import masquerade.sim.model.NullNamespaceResolver;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.impl.SimulationContextImpl;

import org.w3c.dom.Document;

public class TestSimulationContextFactory {
	public static SimulationContext create(Document content) {
		SimulationContext context = new SimulationContextImpl(content, new CompoundConverter(), null, new NullNamespaceResolver());
		context.setContent(content);
		return context;
	}
}
