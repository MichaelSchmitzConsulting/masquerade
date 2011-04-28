package masquerade.sim;

/**
 * Interface for classes able to approve or deny creating an object
 * of a given type with a specified name.
 */
public interface CreateApprover {
	boolean canCreate(Class<?> type, String name, StringBuilder errorMsg);
}
