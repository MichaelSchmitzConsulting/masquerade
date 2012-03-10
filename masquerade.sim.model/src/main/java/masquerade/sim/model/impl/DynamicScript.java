package masquerade.sim.model.impl;

import java.util.List;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * A response script running steps uploaded by a client beforehand, identified
 * by the request ID.
 */
public class DynamicScript extends AbstractProvidedResponseScript {

	private final static StatusLog log = StatusLogger.get(DynamicScript.class);
	
	@Override
	protected Object getResponse(SimulationContext context, String requestId) throws Exception {
		List<SimulationStep> steps = context.getResponseProvider().getResponseScript(requestId);
		if (steps.isEmpty()) {
			log.warning("Empty dynamic response script for request ID " + requestId);
		} else {
			for (SimulationStep step : steps) {
				step.execute(context);
			}
		}
		
		return context.getContent(Object.class);
	}
}
