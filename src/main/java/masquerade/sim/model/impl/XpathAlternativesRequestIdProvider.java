package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.RequestContext;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;

/**
 * {@link RequestIdProvider} fetching a request ID from a list of XPaths, 
 * taking the first matching XPath's value as ID source.
 */
public class XpathAlternativesRequestIdProvider extends AbstractRequestIdProvider<Document> {

	private String xpaths = "";
	
	private transient Collection<String> xpathAlternatives = null;
	
	public XpathAlternativesRequestIdProvider(String name) {
		super(name);
	}

	/**
	 * @return the xpaths
	 */
	public String getXpaths() {
		return xpaths;
	}

	/**
	 * @param xpaths the xpaths to set
	 */
	public synchronized void setXpaths(String xpaths) {
		this.xpaths = xpaths;
		this.xpathAlternatives = null;
	}

	@Override
	public Class<Document> getAcceptedRequestType() {
		return Document.class;
	}

	@Override
	public String getUniqueId(Document request, RequestContext context) {
		for (String xpath : getAlternatives()) {
			String value = evaluate(xpath, request, context.getNamespaceResolver());
			if (isNotEmpty(value)) {
				return value;
			}
		}
		return null;
	}

	private synchronized Collection<String> getAlternatives() {
		if (xpathAlternatives == null) {
			xpathAlternatives = new ArrayList<String>();
			for (String xpath : xpaths.split("\n")) {
				xpath = xpath.trim();
				if (isNotEmpty(xpath)) {
					xpathAlternatives.add(xpath);
				}
			}
		}
		
		return xpathAlternatives;
	}

	private static String evaluate(String xpathExpr, Document request, NamespaceResolver nsResolver) {
		XPath xpath = XPathUtil.createXPath(nsResolver);
		try {
			XPathExpression expr = xpath.compile(xpathExpr);
			return (String) expr.evaluate(request, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Failed to evaluate XPath on request: " + xpathExpr, e);
		}
	}
}
