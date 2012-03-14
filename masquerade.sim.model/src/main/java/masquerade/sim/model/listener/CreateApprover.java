package masquerade.sim.model.listener;

/**
 * Interface for classes able to approve or deny creating an object
 * with a specified id.
 */
public interface CreateApprover {

	/**
	 * @param id
	 * @return <code>true</code> If there is already an object with a name attribute having the given value
	 */
	boolean isNameUsed(String id);
}
