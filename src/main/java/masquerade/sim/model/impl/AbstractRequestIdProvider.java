package masquerade.sim.model.impl;

import masquerade.sim.model.RequestIdProvider;

/**
 * {@link RequestIdProvider} base providing common properties
 * @param <R> Request type
 */
public abstract class AbstractRequestIdProvider<R> implements RequestIdProvider<R> {

	private String name;

	protected AbstractRequestIdProvider(String name) {
		this.name = name;
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
