package masquerade.sim.model.impl.step;

import masquerade.sim.model.SimulationStep;

/**
 * A {@link SimulationStep} executing a JavaScript script that is provided
 * will all variables from the simulation context;
 */
public class JavaScriptStep extends AbstractScriptedStep {

	private String javaScript = "simulation.setContent('')";
	
	public JavaScriptStep(String name) {
		super(name);
	}

	public JavaScriptStep() {
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
		return "javascript";
	}

	@Override
	protected String getScript() {
		return getJavaScript();
	}

	@Override
	public String toString() {
		return getName() + " (Java Script Step)";
	}
}
