package masquerade.sim.model.impl;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * Common base class for scripts providing a response based on the request ID 
 * (usually using a {@link ResponseProvider}).
 */
public abstract class AbstractProvidedResponseScript extends AbstractScript {

	protected static final String EMPTY_RESPONSE = "";
	protected static final StatusLog log = StatusLogger.get(ProvidedResponse.class);

	public AbstractProvidedResponseScript(String name) {
		super(name);
	}

	@Override
	public final Object run(SimulationContext simulationContext) throws Exception {
		String id = simulationContext.getRequestId();
		if (id != null) {
			return getResponse(simulationContext, id);
		} else {
			log.warning("ProvidedResponse Script " + getName() + " - received a request with no valid request ID");
			return EMPTY_RESPONSE;
		}
	}

	protected abstract Object getResponse(SimulationContext simulationContext, String requestId) throws Exception;
}
