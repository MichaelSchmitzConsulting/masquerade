package masquerade.sim.model.impl;

/**
 * Callback used in simulations to send intermediate responses at arbitrary points
 * in time, before the end of a simulation.
 */
public interface ResponseTrigger {
	void sendResponse(Object content) throws Exception;
}
