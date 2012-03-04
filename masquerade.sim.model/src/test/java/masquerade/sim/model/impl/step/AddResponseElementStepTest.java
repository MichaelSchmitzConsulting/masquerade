package masquerade.sim.model.impl.step;

import static org.junit.Assert.assertEquals;


import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationContextStub;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test cases for {@link AddResponseElementStep}
 */
public class AddResponseElementStepTest {

	private static final String VAR_VALUE = "1234";

	/**
	 * Test method for {@link masquerade.sim.model.impl.step.AddResponseElementStep#execute(masquerade.sim.model.SimulationContext)}.
	 */
	@Test
	public void testExecuteOnRoot() throws Exception {
		AddResponseElementStep step = new AddResponseElementStep("test");
		step.setXmlContent("<bla/>");
		
		Document request = DomUtil.parse("<root/>");
		
		SimulationContext context = new SimulationContextStub(request, request);
		step.execute(context);
		
		StepTestSupport.assertElementExists(request, "/root/bla");
	}

	/**
	 * Test method for {@link masquerade.sim.model.impl.step.AddResponseElementStep#execute(masquerade.sim.model.SimulationContext)}.
	 */
	@Test
	public void testExecuteOnParent() throws Exception {
		AddResponseElementStep step = new AddResponseElementStep("test");
		step.setXmlContent("<bla/>");
		step.setParentXpath("/root/child");
		
		Document request = DomUtil.parse("<root><child/></root>");
		
		SimulationContext context = new SimulationContextStub(request, request);
		step.execute(context);
		
		StepTestSupport.assertElementExists(request, "/root/child/bla");
	}

	/**
	 * Test method for {@link masquerade.sim.model.impl.step.AddResponseElementStep#execute(masquerade.sim.model.SimulationContext)}.
	 */
	@Test
	public void testExecuteWithSubstitute() throws Exception {
		AddResponseElementStep step = new AddResponseElementStep("test");
		step.setXmlContent("<child id=\"${myVar}\"/>");
		step.setSubstituteVariables(true);
		
		Document request = DomUtil.parse("<root/>");
		
		SimulationContext context = new SimulationContextStub(request, request);
		context.setVariable("myVar", VAR_VALUE);
		step.execute(context);
		
		Document response = context.getContent(Document.class);
		
		StepTestSupport.assertElementExists(response, "/root/child");
		
		Element child = (Element) response.getDocumentElement().getElementsByTagName("child").item(0);
		assertEquals(VAR_VALUE, child.getAttribute("id"));
	}
}
