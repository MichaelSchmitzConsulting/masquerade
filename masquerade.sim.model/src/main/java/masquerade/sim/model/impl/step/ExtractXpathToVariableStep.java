package masquerade.sim.model.impl.step;

import javax.xml.xpath.XPath;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.util.XPathUtil;

import org.w3c.dom.Document;

/**
 * Sets the result of evaluating an XPath expression to a simulation context
 * variable.
 */
public class ExtractXpathToVariableStep extends AbstractSimulationStep {

	private String xpathExpression = "/";
	private String variableName = "undef";

	public ExtractXpathToVariableStep(String name) {
		super(name);
	}

	public ExtractXpathToVariableStep() {
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Document request = context.getRequest(Document.class);
		XPath xpath = XPathUtil.createXPath(context.getNamespaceResolver());
		String result = xpath.evaluate(xpathExpression, request);
		context.setVariable(variableName, result);
	}

	public String getXpathExpression() {
		return xpathExpression;
	}

	public void setXpathExpression(String xpathExpression) {
		this.xpathExpression = xpathExpression;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	@Override
	public String toString() {
		return "ExtractXpathToVariableStep [xpathExpression=" + xpathExpression + "]";
	}
}
