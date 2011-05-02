package masquerade.sim.model.impl.step;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
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

	public RenameXmlNodeStep(String name) {
		super(name);
	}

	/**
	 * @return the namespaceURI
	 */
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
	}

	/**
	 * @param newQualifiedName the newQualifiedName to set
	 */
	public void setNewQualifiedName(String newQualifiedName) {
		this.newQualifiedName = newQualifiedName;
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Document xml = context.getContent(Document.class);
		
		XPath xpath = XPathUtil.createXPath(context.getNamespaceResolver());
		Node node = (Node) xpath.evaluate(selectNodeXpath, xml, XPathConstants.NODE);
		
		// Document root selected? Change selection to root element
		if (node instanceof Document) {
			node = ((Document)node).getDocumentElement();
		}
		
		xml.renameNode(node, namespaceURI, newQualifiedName);
	}

	@Override
	public String toString() {
		return "Rename node to " + newQualifiedName;
	}
}
