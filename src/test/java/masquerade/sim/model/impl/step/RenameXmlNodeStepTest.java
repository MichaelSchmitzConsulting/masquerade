package masquerade.sim.model.impl.step;

import static org.junit.Assert.assertEquals;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.util.DomUtil;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test case fpr {@link RenameXmlNodeStep}
 */
public class RenameXmlNodeStepTest {

	private static final String NS = "http://bla";

	@Test
	public void testExecuteRenameRootElement() throws Exception {
		// Create step under test
		RenameXmlNodeStep step = new RenameXmlNodeStep("test");
		step.setNamespaceURI(NS);
		step.setNewQualifiedName("bla:new");
		step.setSelectNodeXpath("/");
		
		// Execute step
		Document doc = DomUtil.parse("<a><b/></a>");
		SimulationContext context = TestSimulationContextFactory.create(doc);
		step.execute(context);
		
		// Check if root element has been renamed
		Element element = context.getContent(Document.class).getDocumentElement();
		assertEquals("new", element.getLocalName());
		assertEquals("bla", element.getPrefix());
		assertEquals(NS, element.getNamespaceURI());
	}

	@Test
	public void testExecuteRenameChildElement() throws Exception {
		// Create step under test
		RenameXmlNodeStep step = new RenameXmlNodeStep("test");
		step.setNamespaceURI(NS);
		step.setNewQualifiedName("bla:new");
		step.setSelectNodeXpath("/a/b");
		
		// Execute step
		Document doc = DomUtil.parse("<a><b/></a>");
		SimulationContext context = TestSimulationContextFactory.create(doc);
		step.execute(context);
		
		// Check if root element has been renamed
		Element element = (Element) context.getContent(Document.class).getDocumentElement().getChildNodes().item(0);
		assertEquals("new", element.getLocalName());
		assertEquals("bla", element.getPrefix());
		assertEquals(NS, element.getNamespaceURI());
	}
}
