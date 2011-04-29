package masquerade.sim;

/**
 * Aproves or vetoes domain model object deletion
 */
public interface DeleteApprover {
	/**
	 * @param obj Object to be deleted
	 * @param errorMsg Set an error message to be displayed to the user here
	 * @return <code>true</code> if this object can be deleted, <code>false</code> to veto deletion (set error msg in this case)
	 */
	boolean canDelete(Object obj, StringBuilder errorMsg);
}
