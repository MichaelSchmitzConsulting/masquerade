package masquerade.sim.model.impl;

import masquerade.sim.model.RequestMapping;

/**
 * {@link RequestMapping} base providing common properties
 */
public abstract class AbstractRequestMapping<R> implements RequestMapping<R> {

	private String name;
	private Class<R> acceptedRequestType;
	
	protected AbstractRequestMapping(String name, Class<R> acceptedRequestType) {
	    this.name = name;
	    this.acceptedRequestType = acceptedRequestType;
    }

	@Override
	public Class<R> acceptedRequestType() {
		return acceptedRequestType;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return toString();
	}
}
