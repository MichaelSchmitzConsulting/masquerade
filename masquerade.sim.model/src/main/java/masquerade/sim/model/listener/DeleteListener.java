package masquerade.sim.model.listener;

/**
 * Listener notified when domain model
 * objects are deleted
 */
public interface DeleteListener {
	void notifyDelete(Object obj);
}
