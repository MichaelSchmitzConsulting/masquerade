package masquerade.sim.model.impl;

import masquerade.sim.model.RequestMapping;

/**
 * {@link RequestMapping} base providing common properties
 */
public abstract class AbstractRequestMapping<R> implements RequestMapping<R> {

	private Class<R> acceptedRequestType;
	
	protected AbstractRequestMapping(Class<R> acceptedRequestType) {
	    this.acceptedRequestType = acceptedRequestType;
    }

	@Override
	public Class<R> acceptedRequestType() {
		return acceptedRequestType;
	}
}
