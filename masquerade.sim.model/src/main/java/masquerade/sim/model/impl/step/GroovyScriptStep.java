package masquerade.sim.model.impl.step;

public class GroovyScriptStep extends AbstractScriptedStep {

	private String groovyScript = "simulation.setContent(\"\")";
	
	public GroovyScriptStep(String name) {
		super(name);
	}

	public GroovyScriptStep() {
	}

	/**
	 * @return The Groovy Script
	 */
	public String getGroovyScript() {
		return groovyScript;
	}

	/**
	 * @param groovyScript The Groovy Script
	 */
	public void setGroovyScript(String groovyScript) {
		this.groovyScript = groovyScript;
	}

	@Override
	protected String getScriptLanguage() {
		return "groovy";
	}

	@Override
	protected String getScript() {
		return getGroovyScript();
	}
}
