package masquerade.sim.model.impl.step;

import masquerade.sim.model.SimulationStep;

/**
 * A {@link SimulationStep} executing a JavaScript script that is provided
 * will all variables from the simulation context;
 */
public class JavaScriptStep extends AbstractScriptedStep {

	private String javaScript = "_context.setContent('')";
	
	public JavaScriptStep(String name) {
		super(name);
	}

	/**
	 * @param script the script to set
	 */
	public void setJavaScript(String javaScript) {
		this.javaScript = javaScript;
	}

	public String getJavaScript() {
		return javaScript;
	}

	@Override
	public String getScriptLanguage() {
		return "JavaScript";
	}

	@Override
	protected String getScript() {
		return javaScript;
	}

	@Override
	public String toString() {
		return "Java Script Step";
	}
}
