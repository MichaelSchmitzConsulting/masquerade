package masquerade.sim.model.impl;

import masquerade.sim.model.Channel;

/**
 * {@link Channel} implementation defining common properties
 */
public abstract class AbstractChannel implements Channel {

	private String id;
	private String description = "";

	protected AbstractChannel(String id) {
		this.id = id;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}
}
