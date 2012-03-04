package masquerade.sim.model.impl.step;

import static masquerade.sim.model.impl.step.StepTestSupport.assertElementNotExists;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationContextStub;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test case for {@link RemoveXmlNodeStep}
 */
public class RemoveXmlNodeStepTest {

	@Test
	public void testExecute() throws Exception {
		RemoveXmlNodeStep step = new RemoveXmlNodeStep("test");
		step.setNodeXpathh("/root/child");
		
		Document request = DomUtil.parse("<root><child/></root>");
		
		SimulationContext context = new SimulationContextStub(request, request);
		step.execute(context);
		
		assertElementNotExists(request, "/root/child");
	}
}
