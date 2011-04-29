package masquerade.sim.util;

import masquerade.sim.CreateApprover;
import masquerade.sim.DeleteApprover;

/**
 * Simple implementation of {@link CreateApprover} always returning true
 */
public class AlwaysApprover implements CreateApprover, DeleteApprover {

	@Override
	public boolean canCreate(Class<?> type, String name, StringBuilder errorMsg) {
		return true;
	}

	@Override
	public boolean canDelete(Object obj, StringBuilder errorMsg) {
		return true;
	}

}
