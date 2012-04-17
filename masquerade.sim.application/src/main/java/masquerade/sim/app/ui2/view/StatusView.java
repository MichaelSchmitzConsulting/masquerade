package masquerade.sim.app.ui2.view;

import java.util.Collection;

import masquerade.sim.app.ui.Refreshable;
import masquerade.sim.status.Status;

/**
 * Interface for a view showing current simulator status messages/logs
 */
public interface StatusView {

	public interface StatusViewCallback extends Refreshable {
		void onClear();
	}
	
	/**
	 * Update shown status messages
	 * @param statusLog
	 */
	void refresh(Collection<Status> statusLog);

}
