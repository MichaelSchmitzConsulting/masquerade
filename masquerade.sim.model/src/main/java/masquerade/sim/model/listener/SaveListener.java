package masquerade.sim.model.listener;


/**
 * Interface for listeners that react to events
 * originating from a user's request to save some changes.
 */
public interface SaveListener {
	public void onSave(Object value);
}
