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
	
	/**
	 * Substitutes all variables known in this context. Leaves unknown variables as
	 * they are (does not substitute them with the empty string). Variable values
	 * are converted to String using a {@link Converter} (which falls back to {@link Object#toString()}
	 * if no converter for the variable type is registered).
	 * 
	 * @param content Text to substitute variables in
	 * @return Text with variables substituted
	 */
	String substituteVariables(String content);
}