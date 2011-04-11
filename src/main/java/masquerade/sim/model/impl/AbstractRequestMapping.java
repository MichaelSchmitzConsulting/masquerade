package masquerade.sim.model.impl;

import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;

/**
 * {@link RequestMapping} base providing common properties
 */
public abstract class AbstractRequestMapping<R> implements RequestMapping<R> {

	private String name;
	private ResponseSimulation responseSimulation;
	private Class<? extends R> acceptedRequestType;
	
	protected AbstractRequestMapping(String name, ResponseSimulation responseSimulation, Class<? extends R> acceptedRequestType) {
	    this.name = name;
	    this.responseSimulation = responseSimulation;
	    this.acceptedRequestType = acceptedRequestType;
    }

	@Override
    public boolean accepts(Class<?> requestType) {
		return acceptedRequestType.isAssignableFrom(requestType);
    }

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return toString();
	}

	@Override
	public final ResponseSimulation getResponseSimulation() {
		return responseSimulation;
	}

	public void setResponseSimulation(ResponseSimulation responseSimulation) {
		this.responseSimulation = responseSimulation;
	}
}
