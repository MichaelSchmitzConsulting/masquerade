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
 * {@link SimulationStep} replacing an element specified by an XPath in the response
 */
public class ReplaceElementStep extends AbstractSubstitutingStep {

	private volatile String existingNodeXpath = "";
	private volatile String xmlContent = ""; // Modelled as String to be able to edit it directly on the client, 
	                           // and to not introduce org.w3c.dom dependency when storing in database
	
	private transient ThreadLocalCache<XPathExpression> xpathCache = new ThreadLocalCache<XPathExpression>();
	
	public ReplaceElementStep(String name) {
		super(name);
	}

	@Override
    public void execute(SimulationContext context) throws Exception {
		Element response = parseResponseElement(context);
		
	    Document content = context.getContent(Document.class);
	    
	    Element existing;
	    if (!isEmpty(existingNodeXpath)) {
	    	existing = evaluateExistingNodeXpath(content, context.getNamespaceResolver());
	    } else {
	    	existing = content.getDocumentElement();
	    }

	    if (existing != null) {
		    response = (Element) content.adoptNode(response);
			existing.getParentNode().replaceChild(response, existing);			
	    }
	    
	    context.setContent(content);
    }

	public String getXmlContent() {
		return xmlContent;
	}

	public void setXmlContent(String xmlContent) {
		this.xmlContent = xmlContent;
	}

	public String getExistingNodeXpath() {
		return existingNodeXpath;
	}

	public void setExistingNodeXpath(String existingNodeXpath) {
		this.existingNodeXpath = existingNodeXpath;
		xpathCache.clear();
	}

	@Override
	public String toString() {
		return "ReplaceResponseElementStep [existingNodeXpath=" + existingNodeXpath + ", xmlContent=" + xmlContent + ", getName()=" + getName() + "]";
	}

	private Element evaluateExistingNodeXpath(Document content, NamespaceResolver nsResolver) throws XPathExpressionException {
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
		return xpath.compile(existingNodeXpath);
	}

	private Element parseResponseElement(SimulationContext context) {
		String content = substituteVariables(xmlContent, context);
		return DomUtil.parse(content).getDocumentElement();
	}	
}
