package masquerade.sim.app.ui.presenter;

import masquerade.sim.app.ui.view.StatusView;
import masquerade.sim.app.ui.view.StatusView.StatusViewCallback;
import masquerade.sim.status.StatusLogger;

/**
 * Presenter for {@link StatusView}
 */
public class StatusViewPresenter implements StatusViewCallback {
	private final StatusView view;

	public StatusViewPresenter(StatusView view) {
		this.view = view;
	}

	@Override
	public void onRefresh() {
		view.refresh(StatusLogger.REPOSITORY.latestStatusLogs());
	}

	@Override
	public void onClear() {
		StatusLogger.REPOSITORY.clear();
		view.refresh(StatusLogger.REPOSITORY.latestStatusLogs());
	}
}
