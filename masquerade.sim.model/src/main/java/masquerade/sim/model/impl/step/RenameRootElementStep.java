package masquerade.sim.model.impl.step;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * {@link SimulationStep} renaming the root element of and XML {@link Document}. 
 */
public class RenameRootElementStep extends AbstractSimulationStep {

	private String elementName = "newName";
	private String namespacePrefix = "pfx";
	private String namespaceURI = "http://example.com/ns";
	
	public RenameRootElementStep(String name) {
		super(name);
	}
	
	@Override
	public void execute(SimulationContext context) throws Exception {
		Document doc = context.getContent(Document.class);
		Element root = doc.getDocumentElement();
		if (root != null) {
			doc.renameNode(root, namespaceURI, qualifiedName());
		}
	}

	private String qualifiedName() {
		if (StringUtils.isNotEmpty(namespacePrefix)) {
			return namespacePrefix + ":" + elementName;
		} else {
			return elementName;
		}
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	public void setNamespacePrefix(String namespacePrefix) {
		this.namespacePrefix = namespacePrefix;
	}

	public String getNamespaceURI() {
		return namespaceURI;
	}

	public void setNamespaceURI(String namespaceURI) {
		this.namespaceURI = namespaceURI;
	}
}
