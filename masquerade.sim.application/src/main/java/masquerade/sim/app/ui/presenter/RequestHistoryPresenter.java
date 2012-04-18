package masquerade.sim.app.ui2.presenter;

import java.io.IOException;
import java.io.InputStream;

import masquerade.sim.app.binding.RequestHistoryContainerFactory;
import masquerade.sim.app.ui2.view.RequestHistoryView;
import masquerade.sim.app.ui2.view.RequestHistoryView.RequestHistoryViewCallback;
import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;

import org.apache.commons.io.IOUtils;

/**
 * Presenter for {@link RequestHistoryView}
 */
public class RequestHistoryPresenter implements RequestHistoryViewCallback {
	private final RequestHistoryView view;
	private final RequestHistory history;

	public RequestHistoryPresenter(RequestHistoryView view, RequestHistory history) {
		this.view = view;
		this.history = history;
	}

	@Override
	public void onRefresh() {
		RequestHistoryContainerFactory containerFactory = new RequestHistoryContainerFactory(history);
		view.setData(containerFactory.createContainer());
	}

	@Override
	public void onClear() {
		history.clear();
		onRefresh();
	}

	@Override
	public void onShowEntry(HistoryEntry historyEntry, boolean isRequest) {
		String content;
		try {
			InputStream stream = isRequest ? historyEntry.readRequestData() : historyEntry.readResponseData();
			content = IOUtils.toString(stream);
			String title = (isRequest ? "Request" : "Response") + " Viewer";
			view.showHistoryEntry(title, content);
		} catch (IOException e) {
			view.showError("Unable to retrieve request: " + e.getMessage());
		}
	}
}
