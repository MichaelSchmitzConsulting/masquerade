package masquerade.sim;

public interface UpdateListener {
	/**
	 * Called after the form in the detail view is commited
	 * @param obj
	 */
	void notifyUpdated(Object obj);
}