package masquerade.sim.model.impl.step;

public class RubyScriptStep extends AbstractScriptedStep {

	private String rubyScript = "$simulation.setContent(\"\")";
	
	public RubyScriptStep(String name) {
		super(name);
	}
	
	public RubyScriptStep() {
	}

	/**
	 * @return The Ruby Script
	 */
	public String getRubyScript() {
		return rubyScript;
	}

	/**
	 * @param rubyScript The Ruby Script
	 */
	public void setRubyScript(String rubyScript) {
		this.rubyScript = rubyScript;
	}

	@Override
	protected String getScript() {
		return rubyScript;
	}

	@Override
	protected String getScriptLanguage() {
		return "jruby";
	}
}
