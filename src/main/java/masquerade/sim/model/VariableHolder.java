package masquerade.sim.model;

import java.util.Map;

/**
 * Holds variables e.g. configuration settings variables, or simulation script variables.
 */
public interface VariableHolder {

	/**
	 * @return An unmodifiable {@link Map} containing all variables in this context
	 */
	Map<String, Object> getVariables();

}