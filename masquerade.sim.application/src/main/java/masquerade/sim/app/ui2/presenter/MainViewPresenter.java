package masquerade.sim.app.ui2.presenter;

import masquerade.sim.app.ui2.dialog.ImportExportPresenter;
import masquerade.sim.app.ui2.dialog.view.ImportExportView;
import masquerade.sim.app.ui2.dialog.view.impl.ImportExportViewImpl;
import masquerade.sim.app.ui2.view.MainView;
import masquerade.sim.app.ui2.view.MainView.MainViewCallback;
import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.repository.ModelRepository;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * Presenter for {@link MainView}
 */
public class MainViewPresenter implements MainViewCallback {

	private final ModelRepository modelRepository;
	private final Application application;
	private final Window window;
	private final Importer importer;
	private final ChannelListenerRegistry channelListenerRegistry;

	public MainViewPresenter(ModelRepository modelRepository, Application application, Window window, Importer importer, ChannelListenerRegistry channelListenerRegistry) {
		this.modelRepository = modelRepository;
		this.application = application;
		this.window = window;
		this.importer = importer;
		this.channelListenerRegistry = channelListenerRegistry;
	}

	@Override
	public void onImportExport() {
		ImportExportView view = new ImportExportViewImpl(window);
		ImportExportPresenter presenter = new ImportExportPresenter(view, modelRepository, application, importer, channelListenerRegistry);
		view.bind(presenter);
		presenter.showDialog();
	}

}
