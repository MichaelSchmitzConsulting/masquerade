package masquerade.sim.app.ui2.view;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;

import masquerade.sim.app.ui2.Refreshable;
import masquerade.sim.model.history.HistoryEntry;

/**
 * View showing a history of received requests and sent responses
 */
public interface RequestHistoryView {
	public interface RequestHistoryViewCallback extends Refreshable {
		void onClear();
		void onShowEntry(HistoryEntry historyEntry, boolean isRequest);
	}

	/**
	 * @param container A filterable {@link Container} with RequestHistory entry beans
	 */
	void setData(Filterable container);

	void showError(String msg);

	void showHistoryEntry(String title, String content);
}
