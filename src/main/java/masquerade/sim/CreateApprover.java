package masquerade.sim;

/**
 * Interface for classes able to approve or deny creating an object
 * of a given type with a specified name.
 */
public interface CreateApprover {
	/**
	 * @param type Type of the object to be created
	 * @param name Name of the object to be created
	 * @param errorMsg Set an error message to be displayed to the user here
	 * @return <code>true</code> if this object can be created, <code>false</code> to veto creation (set error msg in this case)
	 */
	boolean canCreate(Class<?> type, String name, StringBuilder errorMsg);
}
