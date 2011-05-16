package masquerade.sim.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import masquerade.sim.converter.CompoundConverter;
import masquerade.sim.model.NullNamespaceResolver;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test cases fir {@link XpathAlternativesRequestIdProvider}
 */
public class XpathAlternativesRequestIdProviderTest {

	@Test
	public void testGetUniqueId() throws Exception {
		XpathAlternativesRequestIdProvider provider = new XpathAlternativesRequestIdProvider("Test");
		provider.setXpaths("//@id1\n//@id2");
		
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc1 = createDocument(docBuilder);
		Document doc2 = createDocument(docBuilder);
		Document doc3 = createDocument(docBuilder);
		
		// Test case with single attribute (second xpath)
		doc1.getDocumentElement().setAttribute("id2", "123");
		
		// Test case with two matching attributes, first one in list of xpaths must match
		doc2.getDocumentElement().setAttribute("id2", "bad");
		Element bla = doc2.createElement("bla");
		bla.setAttribute("id1", "456");
		doc2.getDocumentElement().appendChild(bla);
		
		RequestContextImpl context = new RequestContextImpl(new NullNamespaceResolver(), new CompoundConverter());
		
		String id1 = provider.getUniqueId(doc1, context);
		String id2 = provider.getUniqueId(doc2, context);
		String noId = provider.getUniqueId(doc3, context);
		
		assertEquals("123", id1);
		assertEquals("456", id2);
		assertNull(noId);
	}

	private static Document createDocument(DocumentBuilder docBuilder) {
		Document doc = docBuilder.newDocument();
		Element root = doc.createElement("root");
		doc.appendChild(root);
		return doc;
	}

}
