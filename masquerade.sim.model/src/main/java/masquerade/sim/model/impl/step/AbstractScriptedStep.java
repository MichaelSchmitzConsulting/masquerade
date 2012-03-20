package masquerade.sim.model.impl.step;

import java.util.Map;

import javax.script.ScriptEngine;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.script.ScriptEngineProvider;

/**
 * Base class for JSR-233 script language steps. 
 */
public abstract class AbstractScriptedStep extends AbstractSimulationStep {

	private static final String CONTEXT_VAR_NAME = "simulation";

	private String description = "";
	
	public AbstractScriptedStep(String name) {
		super(name);
	}

	public AbstractScriptedStep() {
	}

	/**
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return description;
	}

	// TODO: Refactor to use annotation
	public String getDocumentation() {
		return "<span>Available variables: " +
			"<a href=\"http://masqueradesim.googlecode.com/svn/trunk/javadoc/masquerade/sim/model/SimulationContext.html\" target=\"_blank\">" +
			"simulation</a>, Provides access to configuration variables from settings and all variables set on the context by previous steps, as well as request/response content.</span>.";
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Script content
	 */
	protected abstract String getScript();

	/**
	 * @return The name of a JSR 223 script language (e.g. "JavaScript")
	 */
	protected abstract String getScriptLanguage();

	@Override
	public void execute(SimulationContext context) throws Exception {
		String lang = getScriptLanguage();
		ScriptEngine engine = ScriptEngineProvider.getScriptEngineManager().getEngineByName(lang);
		
		if (engine == null) {
			throw new MissingScriptEngineException(
					"Scripting language (JSR 223) " + lang + " is not installed. " +
					"Please install the plugin for this scripting language.");
		}
		
		bindVariables(engine, context);
		
		engine.eval(getScript());
	}

	/**
	 * Binds all variables from the {@link SimulationContext} 
	 * to the script engine.
	 * @param engine 
	 * @param context
	 */
	protected void bindVariables(ScriptEngine engine, SimulationContext context) {
		// Bind all variables from the context to the script engine
		Map<String, Object> variables = context.getVariables();
		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			engine.put(entry.getKey(), entry.getValue());
		}

		// Bind the context itself to the '_context' variable
		engine.put(CONTEXT_VAR_NAME, context);
	}
}
