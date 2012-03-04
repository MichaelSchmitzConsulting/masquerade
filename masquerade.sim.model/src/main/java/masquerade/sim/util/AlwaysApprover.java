package masquerade.sim.util;

import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.DeleteApprover;

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

	/* (non-Javadoc)
	 * @see masquerade.sim.model.listener.CreateApprover#isNameUsed(java.lang.Class, java.lang.String)
	 */
	@Override
	public boolean isNameUsed(Class<?> baseType, String usedName) {
		return false;
	}

}
