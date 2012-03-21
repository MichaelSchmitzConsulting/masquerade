package masquerade.sim.model.impl.step;

import static org.apache.commons.lang.StringUtils.isEmpty;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.util.ThreadLocalCache;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * {@link SimulationStep} removing a XML node specified by an XPath
 */
public class RemoveXmlNodeStep extends AbstractSubstitutingStep {

	private volatile String nodeXpath = "";
	
	private transient ThreadLocalCache<XPathExpression> xpathCache = new ThreadLocalCache<XPathExpression>();
	
	public RemoveXmlNodeStep(String name) {
		super(name);
	}

	public RemoveXmlNodeStep() {
	}

	@Override
    public void execute(SimulationContext context) throws Exception {
		if (isEmpty(nodeXpath)) {
			return;
		}
		
		Document content = context.getContent(Document.class);
	    Node existing = evaluateExistingNodeXpath(content, context.getNamespaceResolver());

	    if (existing != null) {
			existing.getParentNode().removeChild(existing);
			context.setContent(content);
	    }	    
    }

	public String getNodeXpath() {
		return nodeXpath;
	}

	public void setNodeXpathh(String nodeXpath) {
		this.nodeXpath = nodeXpath;
		xpathCache.clear();
	}

	@Override
	public String toString() {
		return "RemoveXmlNodeStep [nodeXpath=" + nodeXpath + ", getName()=" + getName() + "]";
	}

	private Node evaluateExistingNodeXpath(Document content, NamespaceResolver nsResolver) throws XPathExpressionException {
		XPathExpression xpathExpr = xpathCache.get();
		if (xpathExpr == null) {
			xpathExpr = createXPath(nsResolver);
			xpathCache.put(xpathExpr);
		}
		
		return (Node) xpathExpr.evaluate(content, XPathConstants.NODE);
	}

	private XPathExpression createXPath(NamespaceResolver namespaceResolver) throws XPathExpressionException {
		XPath xpath = XPathUtil.createXPath(namespaceResolver);
		return xpath.compile(nodeXpath);
	}
}
