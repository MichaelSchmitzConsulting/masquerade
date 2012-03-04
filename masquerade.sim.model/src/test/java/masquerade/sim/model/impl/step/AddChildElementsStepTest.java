package masquerade.sim.model.impl.step;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.SimulationContextStub;
import masquerade.sim.util.DomUtil;
import masquerade.sim.util.XPathUtil;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test cases for {@link AddChildElementsStep}
 */
public class AddChildElementsStepTest {

	@Test
	public void testExecute() throws Exception {
		AddChildElementsStep step = new AddChildElementsStep("test");
		step.setParentXpath("/root/x");
		step.setXmlContent("<ignored><child1/><child2/></ignored>");
		
		// Run twice to test with and w/o cache hit
		assertResult(execute(step));
		assertResult(execute(step));
	}

	private SimulationContextStub execute(AddChildElementsStep step) throws Exception {
		Document doc = DomUtil.parse("<root><x/><y/></root>");
		SimulationContextStub context = new SimulationContextStub(doc, doc);
		step.execute(context);
		return context;
	}

	private void assertResult(SimulationContextStub context) throws XPathExpressionException {
		Document result = context.getContent(Document.class);
		assertTrue(XPathUtil.evaluateBoolean("/root/y", result));
		assertTrue(XPathUtil.evaluateBoolean("/root/x/child1", result));
		assertTrue(XPathUtil.evaluateBoolean("/root/x/child2", result));
		assertEquals(2, result.getElementsByTagName("x").item(0).getChildNodes().getLength());
	}

}
