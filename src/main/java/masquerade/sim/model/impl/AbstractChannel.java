package masquerade.sim.model.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.model.Channel;
import masquerade.sim.model.RequestMapping;

/**
 * {@link Channel} implementation defining common properties
 */
public abstract class AbstractChannel implements Channel {

	private String name;
	private Set<RequestMapping<?>> requestMappings = new LinkedHashSet<RequestMapping<?>>();

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
	public final Set<RequestMapping<?>> getRequestMappings() {
		return new LinkedHashSet<RequestMapping<?>>(requestMappings);
	}

	@Override
	public void setRequestMappings(Set<RequestMapping<?>> requestMappings) {
		if (requestMappings == null) {
			this.requestMappings = new LinkedHashSet<RequestMapping<?>>();
		} else {
			this.requestMappings = new LinkedHashSet<RequestMapping<?>>(requestMappings);
		}
	}
}
