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
import masquerade.sim.util.ThreadLocalCache;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;

/**
 * {@link RequestIdProvider} fetching a request ID from a list of XPaths, 
 * taking the first matching XPath's value as ID source.
 */
public class XpathAlternativesRequestIdProvider implements RequestIdProvider<Document> {

	private String xpaths = "";
	
	/** XPath per-thread cache to avoid recompiling XPaths for every request */
	private transient volatile ThreadLocalCache<Collection<XPathExpression>> xpathCache;
	
	/**
	 * @return A string with a list of XPaths separated by line separators
	 */
	public String getXpaths() {
		return xpaths;
	}

	/**
	 * @param xpaths A list of XPaths separated by line separators
	 */
	public synchronized void setXpaths(String xpaths) {
		this.xpaths = xpaths;
		this.xpathCache().clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Document> getAcceptedRequestType() {
		return Document.class;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Determines a request ID by evaluating a list of XPaths until one of them returns a non-empty result
	 */
	@Override
	public String getUniqueId(Document request, RequestContext context) {
		for (XPathExpression expr : getAlternatives(context.getNamespaceResolver())) {
			String value = evaluate(expr, request);
			if (isNotEmpty(value)) {
				return value;
			}
		}
		return null;
	}

	/**
	 * Compiles all xpaths for later use
	 * 
	 * @param namespaceResolver {@link NamespaceResolver} to use
	 * @return A collection of {@link XPathExpression}
	 */
	private Collection<XPathExpression> getAlternatives(NamespaceResolver namespaceResolver) {
		Collection<XPathExpression> all = xpathCache().get();
		
		if (all == null) {
			Collection<String> xpathAlternatives = parseAlternatives();
			all = compileXpaths(namespaceResolver, xpathAlternatives);
			xpathCache().put(all);
		}
		
		return all;
	}

	/**
	 * Parses the list of xpaths as entered by the user and separated by line separators
	 * into a collection.
	 * @return Collections of xpath expression strings
	 */
	private Collection<String> parseAlternatives() {
		Collection<String> xpathAlternatives = new ArrayList<String>();
		for (String xpath : xpaths.split("\n")) {
			xpath = xpath.trim();
			if (isNotEmpty(xpath)) {
				xpathAlternatives.add(xpath);
			}
		}
		return xpathAlternatives;
	}
	
	private ThreadLocalCache<Collection<XPathExpression>> xpathCache() { 
		if (xpathCache == null) {
			xpathCache = new ThreadLocalCache<Collection<XPathExpression>>();
		}
		return xpathCache;
	}

	/**
	 * Compiles a {@link Collection} of XPath expression strings
	 * 
	 * @param namespaceResolver {@link NamespaceResolver} to use
	 * @param xpathAlternatives {@link Collection} of XPath expresion strings
	 * @return All XPaths compiled to an {@link XPathExpression}
	 * @throws IllegalAccessError If an invalid XPath expression string is encountered
	 */
	private static Collection<XPathExpression> compileXpaths(NamespaceResolver namespaceResolver, Collection<String> xpathAlternatives) {
		Collection<XPathExpression> all = new ArrayList<XPathExpression>(xpathAlternatives.size());
		for (String str : xpathAlternatives) {
			XPath xpath = XPathUtil.createXPath(namespaceResolver);
			XPathExpression expr;
			try {
				expr = xpath.compile(str);
			} catch (XPathExpressionException e) {
				throw new IllegalArgumentException("Failed to compile XPath: " + str, e);
			}
			all.add(expr);
		}
		return all;
	}

	/**
	 * Evaluates an {@link XPathExpression} on a {@link Document}
	 * 
	 * @param expr {@link XPathExpression} to evaluate
	 * @param request {@link Document} to evaluate the XPath against
	 * @return Result of the XPath evaluation (see {@link XPathExpression#evaluate(Object, javax.xml.namespace.QName)}
	 */
	private static String evaluate(XPathExpression expr, Document request) {
		try {
			return (String) expr.evaluate(request, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Failed to evaluate XPath on request: " + expr, e);
		}
	}
}
