package masquerade.sim.app.ui2.presenter;

import masquerade.sim.app.ui.PluginDialog;
import masquerade.sim.app.ui2.dialog.ImportExportPresenter;
import masquerade.sim.app.ui2.dialog.view.ImportExportView;
import masquerade.sim.app.ui2.dialog.view.impl.ImportExportViewImpl;
import masquerade.sim.app.ui2.dialog.view.impl.SettingsDialog;
import masquerade.sim.app.ui2.view.MainView;
import masquerade.sim.app.ui2.view.MainView.MainViewCallback;
import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Settings;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.listener.UpdateListener;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.settings.SettingsProvider;
import masquerade.sim.plugin.PluginManager;

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
	private final PluginManager pluginManager;
	private final SettingsProvider settingsProvider;
	private final SettingsChangeListener settingsChangeListener;
	private final String versionInformation;
	
	public MainViewPresenter(ModelRepository modelRepository, Application application, Window window, Importer importer,
			ChannelListenerRegistry channelListenerRegistry, PluginManager pluginManager, SettingsProvider settingsProvider,
			SettingsChangeListener settingsChangeListener, String versionInformation) {
		this.modelRepository = modelRepository;
		this.application = application;
		this.window = window;
		this.importer = importer;
		this.channelListenerRegistry = channelListenerRegistry;
		this.pluginManager = pluginManager;
		this.settingsProvider = settingsProvider;
		this.settingsChangeListener = settingsChangeListener;
		this.versionInformation = versionInformation;
	}

	@Override
	public void onImportExport() {
		ImportExportView view = new ImportExportViewImpl(window);
		ImportExportPresenter presenter = new ImportExportPresenter(view, modelRepository, application, importer, channelListenerRegistry);
		view.bind(presenter);
		presenter.showDialog();
	}

	@Override
	public void onManagePlugins() {
		PluginDialog.showModal(window, pluginManager);
	}

	@Override
	public void onSettings() {
		Settings settings = settingsProvider.getSettings();
		final Settings oldSettings = settings.clone();		
		SettingsDialog.showModal(window, settings, new UpdateListener() {
			@Override public void notifyUpdated(Object obj) {
				Settings settings = (Settings) obj;
				settingsProvider.notifyChanged(settings);
				settingsChangeListener.settingsChanged(oldSettings, settings);
			}
		}, versionInformation);
	}
}
