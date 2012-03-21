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

/**
 * {@link SimulationStep} adding an element to the response under a 
 * parent specified by an XPath
 * TODO: Rename to AddElementStep for consistency with other steps
 */
public class AddResponseElementStep extends AbstractSubstitutingStep {

	private volatile String parentXpath = "";
	private volatile String xmlContent = ""; // Modelled as String to be able to edit it directly on the client, 
	                           // and to not introduce org.w3c.dom dependency when storing in database
	
	private transient ThreadLocalCache<XPathExpression> xpathCache = new ThreadLocalCache<XPathExpression>();
	
	public AddResponseElementStep(String name) {
		super(name);
	}

	public AddResponseElementStep() {
	}

	@Override
    public void execute(SimulationContext context) throws Exception {
	    Document content = context.getContent(Document.class);
	    
	    Element parent;
	    if (!isEmpty(parentXpath)) {
	    	parent = evaluateParentXpath(content, context.getNamespaceResolver());
	    } else {
	    	parent = content.getDocumentElement();
	    }

	    if (parent != null) {
	    	Element response = parseResponseElement(context);
		    response = (Element) content.adoptNode(response);
			parent.appendChild(response);			
	    }
	    
	    context.setContent(content);
    }

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	public String getParentXpath() {
		return parentXpath;
	}

	public void setParentXpath(String parentXpath) {
		this.parentXpath = parentXpath;
		xpathCache.clear();
	}

	@Override
	public String toString() {
		return "AddResponseElementStep [parentXpath=" + parentXpath + ", xmlContent=" + xmlContent + ", getName()=" + getName() + "]";
	}

	private Element evaluateParentXpath(Document content, NamespaceResolver nsResolver) throws XPathExpressionException {
		XPathExpression xpathExpr = xpathCache.get();
		if (xpathExpr == null) {
			xpathExpr = createXPath(nsResolver);
			xpathCache.put(xpathExpr);
		}
		
		Node node = (Node) xpathExpr.evaluate(content, XPathConstants.NODE);
		if (node instanceof Element) {
			return (Element) node;
		}
		
		return null;
	}

	private XPathExpression createXPath(NamespaceResolver namespaceResolver) throws XPathExpressionException {
		XPath xpath = XPathUtil.createXPath(namespaceResolver);
		return xpath.compile(parentXpath);
	}

	private Element parseResponseElement(SimulationContext context) {
		String content = substituteVariables(xmlContent, context);
		return DomUtil.parse(content).getDocumentElement();
	}	
}
