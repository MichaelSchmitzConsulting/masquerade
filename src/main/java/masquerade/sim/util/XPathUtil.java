package masquerade.sim.util;

import javax.xml.xpath.XPath;

import masquerade.sim.model.NamespaceResolver;

import org.springframework.util.xml.SimpleNamespaceContext;

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
}
