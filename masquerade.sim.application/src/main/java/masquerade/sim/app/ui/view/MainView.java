package masquerade.sim.app.ui.view;


/**
 * Interface for the main view wrapping all other views in a tabbed layout and providing
 * import/export, settings and plugins links.
 */
public interface MainView {
	public interface MainViewCallback {
		void onImportExport();
		void onManagePlugins();
		void onSettings();
	}
}
