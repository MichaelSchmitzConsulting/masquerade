package masquerade.sim.model.impl.step;

import static masquerade.sim.model.impl.step.StepTestSupport.assertElementExists;
import static masquerade.sim.model.impl.step.StepTestSupport.assertElementNotExists;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationContextStub;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test case for {@link ReplaceElementStep}
 */
public class ReplaceElementStepTest {

	/**
	 * Test method for {@link masquerade.sim.model.impl.step.ReplaceElementStep#execute(masquerade.sim.model.SimulationContext)}.
	 */
	@Test
	public void testExecute() throws Exception {
		ReplaceElementStep step = new ReplaceElementStep("test");
		step.setExistingNodeXpath("/root/child");
		step.setXmlContent("<newChild/>");
		
		Document request = DomUtil.parse("<root><child/></root>");
		
		SimulationContext context = new SimulationContextStub(request, request);
		step.execute(context);
		
		assertElementExists(request, "/root/newChild");
		assertElementNotExists(request, "/root/child");
	}
}
