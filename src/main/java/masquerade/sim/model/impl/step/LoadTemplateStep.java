package masquerade.sim.model.impl.step;

import java.io.InputStream;

import masquerade.sim.model.FileType;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;

import org.apache.commons.io.IOUtils;

/**
 * A {@link SimulationStep} loading a template from a file as response content.
 */
public class LoadTemplateStep extends AbstractSimulationStep {

	private String templateName = null;
	
	public LoadTemplateStep(String name) {
		super(name);
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * Loads the specified template, and sets it as the response content.
	 * Does not execute any action if template name or variable name
	 * are not set.
	 */
	@Override
	public void execute(SimulationContext context) throws Exception {
		if (templateName == null) {
			return;
		}
		
		InputStream input = context.load(FileType.TEMPLATE, templateName);
		String content = IOUtils.toString(input);
		context.setContent(content);
	}
}
