package masquerade.sim.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.NamespaceResolver;

import org.w3c.dom.Document;

/**
 * XPath-related utility methods
 */
public class XPathUtil {
	/**
	 * Creates an XPath with all namespaces declared from the given resolver
	 * @param resolver {@link NamespaceResolver}
	 * @return An XPath instance
	 */
	public static XPath createXPath(NamespaceResolver resolver) {
		javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		declareNamespaces(xpath, resolver);
		return xpath;
	}

	private static void declareNamespaces(XPath xpath, NamespaceResolver resolver) {
		SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
		nsContext.setBindings(resolver.getKnownNamespaces());
		xpath.setNamespaceContext(nsContext);
	}
	
	/**
	 * Evaluates a boolean xpath. Expensive as it creates a new xpath for every evaluation.
	 */
	public static boolean evaluateBoolean(String xpathExpression, Document doc) throws XPathExpressionException {
		javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Boolean result = (Boolean) xpath.evaluate(xpathExpression, doc, XPathConstants.BOOLEAN);
		return result;
	}
}
