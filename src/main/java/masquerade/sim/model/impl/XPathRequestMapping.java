package masquerade.sim.model.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * Matches requests based on an XPath
 */
public class XPathRequestMapping extends AbstractRequestMapping<Document> {

	private String xpath = "";

	public XPathRequestMapping(String name) {
		super(name, null, Document.class);
	}

	@Override
	public boolean matches(Document request) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
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
		return "XPathRequestMapping > " + getResponseSimulation();
	}
}
