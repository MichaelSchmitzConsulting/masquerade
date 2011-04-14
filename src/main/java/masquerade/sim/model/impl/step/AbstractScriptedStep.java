package masquerade.sim.model.impl.step;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import masquerade.sim.model.SimulationContext;

/**
 * Base class for JSR-233 script language steps. 
 */
public abstract class AbstractScriptedStep extends AbstractSimulationStep {

	private static final String CONTEXT_VAR_NAME = "_context";

	public AbstractScriptedStep(String name) {
		super(name);
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
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName(getScriptLanguage());
		
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
