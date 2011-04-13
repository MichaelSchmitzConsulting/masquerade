package masquerade.sim.model.impl;

import java.io.InputStream;

import masquerade.sim.model.FileType;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;

import org.apache.commons.io.IOUtils;

/**
 * A {@link SimulationStep} loading a template
 * to a context variable.
 */
public class LoadTemplateToVariableStep extends AbstractSimulationStep {
	
	private String templateName = null;
	private String variableName = "template";
	
	public LoadTemplateToVariableStep(String name) {
		super(name);
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	/**
	 * Loads the specified template, and sets it as a context variable.
	 * Does not execute any action if template name or variable name
	 * are not set.
	 */
	@Override
	public void execute(SimulationContext context) throws Exception {
		if (templateName == null || variableName == null) {
			return;
		}
		
		InputStream input = context.load(FileType.TEMPLATE, templateName);
		String content = IOUtils.toString(input);
		context.setVariable(variableName, content);
	}
}
