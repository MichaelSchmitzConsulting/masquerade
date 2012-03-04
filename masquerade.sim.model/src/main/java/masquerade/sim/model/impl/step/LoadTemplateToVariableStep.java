package masquerade.sim.model.impl.step;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.InputStream;

import masquerade.sim.model.FileType;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;

import org.apache.commons.io.IOUtils;

/**
 * A {@link SimulationStep} loading a template
 * to a context variable.
 */
public class LoadTemplateToVariableStep extends AbstractSubstitutingStep {
	
	private String templateName = "";
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
		if (isEmpty(templateName) || isEmpty(variableName)) {
			return;
		}
		
		InputStream input = context.load(FileType.TEMPLATE, templateName);
		String content = IOUtils.toString(input);
		
		content = substituteVariables(content, context);
		
		context.setVariable(variableName, content);
	}

	@Override
	public String toString() {
		return "Load template " + templateName + " to variable " + variableName;
	}
}
