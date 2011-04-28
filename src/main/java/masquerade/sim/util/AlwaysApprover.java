package masquerade.sim.util;

import masquerade.sim.CreateApprover;

/**
 * Simple implementation of {@link CreateApprover} always returning true
 */
public class AlwaysApprover implements CreateApprover {

	@Override
	public boolean canCreate(Class<?> type, String name, StringBuilder errorMsg) {
		return true;
	}

}
