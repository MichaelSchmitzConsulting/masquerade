package masquerade.sim.model.impl;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.RequestContext;
import masquerade.sim.util.ThreadLocalCache;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;

/**
 * Matches requests based on an XPath
 */
public class XPathRequestMapping extends AbstractRequestMapping<Document> {

	private volatile String xpath = "";
	private transient ThreadLocalCache<XPathExpression> xpathCache;
	
	public XPathRequestMapping() {
		super(Document.class);
	}

	/**
	 * Matches the request if the xpath for this mapping evaluates to <code>true</code>
	 */
	@Override
	public boolean matches(Document request, RequestContext requestContext) {
		XPathExpression xpath = xpathCache().get();
		try {
			if (xpath == null) {
				xpath = createXPath(requestContext.getNamespaceResolver());
				xpathCache().put(xpath);
			}
			return (Boolean) xpath.evaluate(request, XPathConstants.BOOLEAN);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Failed to evaluate XPath on request", e);
		}
	}

	public String getMatchXpath() {
		return xpath;
	}

	public void setMatchXpath(String xpath) {
		this.xpath = xpath;
		xpathCache().clear();
	}
	
	private XPathExpression createXPath(NamespaceResolver namespaceResolver) throws XPathExpressionException {
		XPath xpath = XPathUtil.createXPath(namespaceResolver);
		return xpath.compile(this.xpath);
	}
	
	private ThreadLocalCache<XPathExpression> xpathCache() {
		if (xpathCache == null) {
			xpathCache = new ThreadLocalCache<XPathExpression>();
		}
		return xpathCache;
	}
}
