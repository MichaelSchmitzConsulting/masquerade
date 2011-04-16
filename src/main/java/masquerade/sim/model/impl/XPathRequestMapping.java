package masquerade.sim.model.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.RequestContext;
import masquerade.sim.model.Script;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;

/**
 * Matches requests based on an XPath
 */
public class XPathRequestMapping extends AbstractRequestMapping<Document> {

	private String xpath = "";

	public XPathRequestMapping(String name) {
		super(name, null, Document.class);
	}

	/**
	 * Matches the request if the xpath for this mapping evaluates to <code>true</code>
	 */
	@Override
	public boolean matches(Document request, RequestContext requestContext) {
		XPath xpath = XPathUtil.createXPath(requestContext.getNamespaceResolver());
		try {
			XPathExpression expr = xpath.compile(this.xpath);
			return (Boolean) expr.evaluate(request, XPathConstants.BOOLEAN);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Failed to evaluate XPath on request", e);
		}
	}

	public String getMatchXpath() {
		return xpath;
	}

	public void setMatchXpath(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public String toString() {
		Script script = getScript();
		String name = script == null ? "<n/a>" : script.getName();
		return "XPathRequestMapping > " + name;
	}
}
