package masquerade.sim.model.impl.step;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.Optional;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.util.ThreadLocalCache;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * A {@link SimulationStep} renaming the root element
 */
public class RenameXmlNodeStep extends AbstractSubstitutingStep {

	private String namespaceURI = "http://example.com/ns";
	private String newQualifiedName = "ns:newName";
	private String selectNodeXpath = "/";
	
	private transient ThreadLocalCache<XPathExpression> xpathCache;

	public RenameXmlNodeStep(String name) {
		super(name);
	}

	/**
	 * @return the namespaceURI
	 */
	@Optional
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * @param namespaceURI the namespaceURI to set
	 */
	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}

	/**
	 * @return the qualifiedName
	 */
	public String getNewQualifiedName() {
		return newQualifiedName;
	}

	/**
	 * @return the selectNodeXpath
	 */
	public String getSelectNodeXpath() {
		return selectNodeXpath;
	}

	/**
	 * @param selectNodeXpath the selectNodeXpath to set
	 */
	public void setSelectNodeXpath(String selectNodeXpath) {
		this.selectNodeXpath = selectNodeXpath;
		xpathCache().clear();
	}

	/**
	 * @param newQualifiedName the newQualifiedName to set
	 */
	public void setNewQualifiedName(String newQualifiedName) {
		this.newQualifiedName = newQualifiedName;
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Document content = context.getContent(Document.class);
		
		String uri = context.substituteVariables(namespaceURI);
		String qname = context.substituteVariables(newQualifiedName);
		
		XPathExpression xpath = getXpath(selectNodeXpath, context.getNamespaceResolver());
		Node node = (Node) xpath.evaluate(content, XPathConstants.NODE);
		
		// Document root selected? Change selection to root element
		if (node instanceof Document) {
			node = ((Document)node).getDocumentElement();
		}
		
		content.renameNode(node, uri, qname);
		context.setContent(content);
	}

	private XPathExpression getXpath(String xpathExpr, NamespaceResolver namespaceResolver) throws XPathExpressionException {
		XPathExpression expr = xpathCache().get();
		if (expr == null) {
			XPath xpath = XPathUtil.createXPath(namespaceResolver);
			expr = xpath.compile(xpathExpr);
			xpathCache().put(expr);
		}
		return expr;
	}

	@Override
	public String toString() {
		return "Rename node to " + newQualifiedName;
	}
	
	/**
	 * Use this accessor to access the xpath cache, the transient field will be null
	 * if this object is instantiated from persistant state.
	 * 
	 * @return Per-thread cache for {@link XPathExpression}. 
	 */
	private ThreadLocalCache<XPathExpression> xpathCache() {
		ThreadLocalCache<XPathExpression> cache = xpathCache;
		if (cache == null) {
			cache = new ThreadLocalCache<XPathExpression>();
			xpathCache = cache;
		}
		return cache;
	}
}
