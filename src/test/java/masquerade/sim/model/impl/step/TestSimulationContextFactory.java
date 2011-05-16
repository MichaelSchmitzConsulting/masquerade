package masquerade.sim.model.impl.step;

import java.util.Collections;

import masquerade.sim.converter.CompoundConverter;
import masquerade.sim.model.NullNamespaceResolver;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.impl.SimulationContextImpl;

import org.w3c.dom.Document;

/**
 * Creates {@link SimulationContext} instances for use in tests 
 */
public class TestSimulationContextFactory {
	public static SimulationContext create(Document content) {
		SimulationContext context = 
			new SimulationContextImpl(content, Collections.<String, Object>emptyMap(), new CompoundConverter(), null, new NullNamespaceResolver());
		context.setContent(content);
		return context;
	}
}
