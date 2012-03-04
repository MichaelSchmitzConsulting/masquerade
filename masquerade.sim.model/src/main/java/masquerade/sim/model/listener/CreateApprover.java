package masquerade.sim.model.listener;

/**
 * Interface for classes able to approve or deny creating an object
 * of a given type with a specified name.
 */
public interface CreateApprover {
	/**
	 * Checks if the object of the given type can be created without violating any
	 * constraints.
	 * @param type Type of the object to be created
	 * @param name Name of the object to be created
	 * @param errorMsg Set an error message to be displayed to the user here
	 * @return <code>true</code> if this object can be created, <code>false</code> to veto creation (set error msg in this case)
	 */
	boolean canCreate(Class<?> type, String name, StringBuilder errorMsg);

	/**
	 * @param baseType
	 * @param usedName
	 * @return <code>true</code> If there is already an object with a name attribute having the given value
	 */
	boolean isNameUsed(Class<?> baseType, String usedName);
}
