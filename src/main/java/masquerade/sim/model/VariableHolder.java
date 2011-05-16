package masquerade.sim.model;

import java.util.Map;

public interface VariableHolder {

	/**
	 * @return A copy of the {@link Map} containing all variables in this context
	 */
	Map<String, Object> getVariables();

}