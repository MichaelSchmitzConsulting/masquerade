package masquerade.sim.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test cases for {@link DomUtil}
 */
public class DomUtilTest {

	private static final String ROOT = "Root";

	/**
	 * Test method for {@link masquerade.sim.util.DomUtil#createDocument(java.lang.String)}.
	 */
	@Test
	public void testCreateDocument() {
		Document doc = DomUtil.createDocument(ROOT);
		assertEquals(ROOT, doc.getDocumentElement().getTagName());
	}

}
