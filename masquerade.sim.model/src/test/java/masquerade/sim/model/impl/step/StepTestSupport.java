package masquerade.sim.model.impl.step;

import static org.junit.Assert.assertEquals;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Test utility class for common test/assert logic
 */
public class StepTestSupport {

	public static void assertElementExists(Document request, String path) throws XPathExpressionException {
		assertElementSelectCount(request, path, 1);
	}
	
	public static void assertElementNotExists(Document request, String path) throws XPathExpressionException {
		assertElementSelectCount(request, path, 0);
	}

	private static void assertElementSelectCount(Document request, String path, int selectCount) throws XPathExpressionException {
		NodeList nodes = 
			(NodeList) XPathFactory.newInstance().newXPath().evaluate(path, request, XPathConstants.NODESET);
		
		assertEquals("path " + path + " selects " + selectCount + " element(s)", selectCount, nodes.getLength());
	}
}
