package masquerade.sim.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DomUtil {
	private static final String FEATURE_DEFER =
	      "http://apache.org/xml/features/dom/defer-node-expansion";
	  
	public static Document createDocument(String rootElementName) {
		DocumentBuilder db = createDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement(rootElementName);
		doc.appendChild(root);
		return doc;
	}

	public static Document parse(InputStream is) {
		DocumentBuilder db = createDocumentBuilder();
	    try {
			InputSource inStream = new InputSource();

			inStream.setCharacterStream(new InputStreamReader(is));
			return db.parse(inStream);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse XML content", e);
		}
	}
	
	public static Document parse(String xmlStr) {
		DocumentBuilder db = createDocumentBuilder();
	    try {
			InputSource inStream = new InputSource();
 
			inStream.setCharacterStream(new StringReader(xmlStr));
			return db.parse(inStream);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse XML content", e);
		}
	}

	public static void write(Node node, OutputStream os) {
	    TransformerFactory tf = TransformerFactory.newInstance();
	    try {
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(os));
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to write XML document to stream", e);			
		}
	}
	
	public static String asString(Node node) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		write(node, os);
		return new String(os.toByteArray());
	}

	private static DocumentBuilder createDocumentBuilder() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder documentBuilder;
		try {
			// Adopting nodes is troublesome with deferred docs, so it's turned off
			// AddResponseElementStep doesn't work with deferred docs
			factory.setFeature(FEATURE_DEFER, false);
			documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("XML API configuration problem", e);
		}
		return documentBuilder;
	}
}
