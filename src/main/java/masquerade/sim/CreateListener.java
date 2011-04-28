package masquerade.sim;

/**
 * Listener notified when new domain model objects are 
 * created
 */
public interface CreateListener {
	/**
	 * @param value The created domain model object
	 */
	void notifyCreate(Object value);
}
