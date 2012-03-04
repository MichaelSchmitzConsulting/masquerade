package masquerade.sim.model.impl.step;

import static org.junit.Assert.assertEquals;
import masquerade.sim.model.SimulationContextStub;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test case for {@link RenameRootElementStep}
 */
public class RenameRootElementStepTest {

	@Test
	public void testExecute() throws Exception {
		RenameRootElementStep step = new RenameRootElementStep("test");
		step.setElementName("root");
		step.setNamespacePrefix("aaa");
		step.setNamespaceURI("http://test");
		
		Document doc = DomUtil.parse("<x><y/></x>");
		SimulationContextStub context = new SimulationContextStub(doc, doc);
 		step.execute(context);
 		
 		Document result = context.getContent(Document.class);
 		Element el = result.getDocumentElement();
 		assertEquals("root", el.getLocalName());
 		assertEquals("aaa", el.getPrefix());
 		assertEquals("http://test", el.getNamespaceURI());
	}

}
