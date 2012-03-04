package masquerade.sim.model.impl;

import masquerade.sim.model.Channel;

/**
 * {@link Channel} implementation defining common properties
 */
public abstract class AbstractChannel implements Channel {

	private String name;

	protected AbstractChannel(String name) {
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

	@Override
	final public String toString() {
		return getName();
	}	
}
