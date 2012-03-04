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
 * Extract unique request IDs from XML documents using an XPath
 */
public class XPathRequestIdProvider extends AbstractRequestIdProvider<Document> {

	private volatile String xpath = "";
	
	private transient volatile ThreadLocalCache<XPathExpression> xpathCache;
	
	public XPathRequestIdProvider(String name) {
		super(name);
	}

	/**
     * @return the xpath
     */
    public String getXpath() {
    	return xpath;
    }

	/**
     * @param xpath the xpath to set
     */
    public void setXpath(String xpath) {
    	this.xpath = xpath;
    	xpathCache().clear();
    }

    @Override
	public Class<Document> getAcceptedRequestType() {
    	return Document.class;
	}

	/**
     * Returns an unique ID for this request. Determines the ID by evaluating the
     * specified XPath to a String result.
     */
	@Override
    public String getUniqueId(Document request, RequestContext context) {
		XPathExpression xpath = xpathCache().get();
		try {
			if (xpath == null) {
				xpath = createXPath(context.getNamespaceResolver());
				xpathCache().put(xpath);
			}
			return (String) xpath.evaluate(request, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Failed to evaluate XPath on request", e);
		}
    }

	@Override
    public String toString() {
	    return getName() + " (XPath request ID provider)";
    }	
	
	private XPathExpression createXPath(NamespaceResolver namespaceResolver) throws XPathExpressionException {
		XPath xpath = XPathUtil.createXPath(namespaceResolver);
		XPathExpression expr = xpath.compile(this.xpath);
		return expr;
	}
	
	private ThreadLocalCache<XPathExpression> xpathCache() {
		if (xpathCache == null) {
			xpathCache = new ThreadLocalCache<XPathExpression>();
		}
		return xpathCache;
	}
}
