package masquerade.sim.model.impl;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.response.ResponseProvider;

/**
 * A simulation returning a respose uploaded beforehand by a client using 
 * the response provider service. Queries the {@link ResponseProvider} from
 * the {@link SimulationContext} for available responses.
 */
public class ProvidedResponse extends AbstractProvidedResponseScript {
	
	public ProvidedResponse(String name) {
		super(name);
	}
	
	@Override
	protected Object getResponse(SimulationContext simulationContext, String id) {
		Object response = simulationContext.getResponseProvider().getResponse(id);
		if (response == null) {
			log.warning("ProvidedResponse Script " + getName() + " - received a request a a valid request ID (" + id + ") but found no matching response");
			return EMPTY_RESPONSE;
		}
		return response;
	}

	@Override
	public String toString() {
		return getName() + " (provided response)";
	}
}
