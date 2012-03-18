package masquerade.sim.util;

import masquerade.sim.model.listener.CreateApprover;

/**
 * Simple implementation of {@link CreateApprover} always returning true
 */
public class AlwaysApprover implements CreateApprover {

	@Override
	public boolean isNameUsed(String id) {
		return false;
	}
}
