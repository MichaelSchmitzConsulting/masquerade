package masquerade.sim.model.impl.step;

import static org.junit.Assert.assertEquals;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationContextStub;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;

public class ExtractXpathToVariableStepTest {

	private static final String VAR = "var";

	/**
	 * Test method for {@link masquerade.sim.model.impl.step.ExtractXpathToVariableStep#execute(masquerade.sim.model.SimulationContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testExecute() throws Exception {
		ExtractXpathToVariableStep step = new ExtractXpathToVariableStep("test");
		step.setVariableName(VAR);
		step.setXpathExpression("/test/@id");
		
		Document request = DomUtil.createDocument("test");
		request.getDocumentElement().setAttribute("id", "123");
		
		SimulationContext context = new SimulationContextStub(request);
		step.execute(context);
		
		assertEquals("123", context.getVariable(VAR));
	}

}
