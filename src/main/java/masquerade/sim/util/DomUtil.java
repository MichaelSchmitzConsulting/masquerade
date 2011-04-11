package masquerade.sim.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DomUtil {
	public static Document parse(InputStream is) {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inStream = new InputSource();

			inStream.setCharacterStream(new InputStreamReader(is));
			return db.parse(inStream);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to parse XML content", e);
		}
	}
	
	public static Document parse(String xmlStr) {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    try {
			DocumentBuilder db = factory.newDocumentBuilder();
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
}
