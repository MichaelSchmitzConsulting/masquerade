package masquerade.sim.model.impl.step;

import static org.apache.commons.lang.StringUtils.isEmpty;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.util.DomUtil;
import masquerade.sim.util.ThreadLocalCache;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link SimulationStep} adding elements to the response under a parent
 * specified by an XPath. Omits the root element of the provided XML content,
 * thus providing the ability to add multiple children at once under an element
 * (as opposed to {@link AddResponseElementStep}.
 * 
 * <p>
 * Optimized for performance, caches XPath and Element instance, does thus not
 * provide variable substitution.
 */
public class AddChildElementsStep extends AbstractSimulationStep {
	private volatile String parentXpath = "";
	private volatile String xmlContent = ""; // Modelled as String to be able to edit it directly on the client,
												// and to not introduce org.w3c.dom dependency when storing in the object database

	private transient ThreadLocalCache<XPathExpression> xpathCache;
	private transient ThreadLocalCache<Element> elementCache;

	public AddChildElementsStep(String name) {
		super(name);
	}

	public AddChildElementsStep() {
	}

	public String getParentXpath() {
		return parentXpath;
	}

	public void setParentXpath(String parentXpath) {
		this.parentXpath = parentXpath;
		xpathCache().clear();
	}

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
		elementCache().clear();
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Element parent = getParentElement();

		Document content = context.getContent(Document.class);

		Element insertAt;
		if (!isEmpty(parentXpath)) {
			insertAt = evaluateParentXpath(content, context.getNamespaceResolver());
		} else {
			insertAt = content.getDocumentElement();
		}

		if (insertAt != null) {
			insertChildren(parent, insertAt, content);
		}

		context.setContent(content);
	}

	private void insertChildren(Element parent, Element insertUnder, Document owner) {
		boolean anyElementChildrenLeft = true;
		
		while (anyElementChildrenLeft) {
			anyElementChildrenLeft = false;
			NodeList children = parent.getChildNodes();
			for (int i = 0; i < children.getLength(); ++i) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					anyElementChildrenLeft = true;
					Element child = (Element) owner.adoptNode(node);
					insertUnder.appendChild(child);
					// Adopting to target will remove nodes from parent - re-evalute getChildNodes()
					break; // for
				}
			}
		}
	}

	private Element getParentElement() {
		// Cache parsed XML to avoid unnecessary parsing
		Element parent = elementCache().get();
		if (parent == null) {
			parent = DomUtil.parse(xmlContent).getDocumentElement();
			elementCache().put(parent);
		}
		// Clone to avoid modifying cached instance
		return (Element) parent.cloneNode(true);
	}

	/** 
	 * Use this accessor to access the cache. Transient field will
	 * be <code>null</code> if restored from persistant state.
	 */
	private ThreadLocalCache<Element> elementCache() {
		ThreadLocalCache<Element> cache = elementCache;
		if (cache == null) {
			cache = new ThreadLocalCache<Element>();
			elementCache = cache;
		}
		return cache;
	}
	
	private Element evaluateParentXpath(Document content, NamespaceResolver nsResolver) throws XPathExpressionException {
		XPathExpression xpathExpr = xpathCache().get();
		if (xpathExpr == null) {
			xpathExpr = createXPath(nsResolver);
			xpathCache().put(xpathExpr);
		}
		
		Node node = (Node) xpathExpr.evaluate(content, XPathConstants.NODE);
		if (node instanceof Element) {
			return (Element) node;
		}
		
		return null;
	}

	/** 
	 * Use this accessor to access the cache. Transient field will
	 * be <code>null</code> if restored from persistant state.
	 */
	private ThreadLocalCache<XPathExpression> xpathCache() {
		ThreadLocalCache<XPathExpression> cache = xpathCache;
		if (cache == null) {
			cache = new ThreadLocalCache<XPathExpression>();
			xpathCache = cache;
		}
		return cache;
	}
	
	private XPathExpression createXPath(NamespaceResolver namespaceResolver) throws XPathExpressionException {
		XPath xpath = XPathUtil.createXPath(namespaceResolver);
		return xpath.compile(parentXpath);
	}

	public String getDocumentation() {
		return "<span>Inserts the children of the root element specified in the XmlContent property at the XPath position " +
				"specified in the ParentXPath property. Ignores the root element of the XmlContent property.</span>";
	}
}
