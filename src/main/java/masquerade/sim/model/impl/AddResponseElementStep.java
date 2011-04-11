package masquerade.sim.model.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.util.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * {@link SimulationStep} adding an element to the response under a 
 * parent specified by an XPath
 */
public class AddResponseElementStep extends AbstractSimulationStep {

	private String parentXpath = "";
	private String xmlContent = ""; // Modelled as String to be able to edit it directly on the client, 
	                           // and to not introduce org.w3c.dom dependency when storing in database 
	
	public AddResponseElementStep(String name) {
		super(name);
	}

	@Override
    public void execute(SimulationContext context) throws Exception {
		Element response = parseResponseElement();
		
	    Document request = context.getContent(Document.class);
	    request.adoptNode(response);
		request.getDocumentElement().appendChild(response);
    }

	private Element parseResponseElement() throws ParserConfigurationException, SAXException, IOException {
		return DomUtil.parse(xmlContent).getDocumentElement();
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
	}

	@Override
	public String toString() {
		return "AddResponseElementStep [parentXpath=" + parentXpath + ", xmlContent=" + xmlContent + ", getName()=" + getName() + "]";
	}	
}
