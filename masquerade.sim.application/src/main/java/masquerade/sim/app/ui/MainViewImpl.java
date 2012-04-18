package masquerade.sim.app.ui;

import static masquerade.sim.app.ui2.Icons.IMPORTEXPORT;
import static masquerade.sim.app.ui2.Icons.PLUGINS;
import static masquerade.sim.app.ui2.Icons.SETTINGS;

import java.util.HashMap;
import java.util.Map;

import masquerade.sim.app.ui2.Refreshable;
import masquerade.sim.app.ui2.dialog.view.impl.SettingsDialog;
import masquerade.sim.app.ui2.view.MainView;
import masquerade.sim.model.Settings;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.listener.UpdateListener;
import masquerade.sim.model.settings.SettingsProvider;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * The main application layout containing the header and a tab layout for the
 * main content.
 */
@SuppressWarnings("serial")
public class MainViewImpl extends VerticalLayout implements MainView {

	private final TabSheet tabSheet;
	private final Map<Component, Refreshable> refreshMap = new HashMap<Component, Refreshable>();

	public MainViewImpl(final MainViewCallback callback, Resource logo,
			final SettingsChangeListener settingsChangeListener, String baseUrl, 
			final SettingsProvider settingsProvider, final String versionInformation) {
		
		setSizeFull();
		setMargin(true);

		// Header
		HorizontalLayout header = new HorizontalLayout();
		header.setWidth("100%");
		header.setSpacing(true);
		
		// Logo
		Embedded image = new Embedded(null, logo);
		image.setWidth("502px");
		image.setHeight("52px");
		header.addComponent(image);
		header.setExpandRatio(image, 1.0f);
		
		// Plugins link
		Button.ClickListener pluginsListener = new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				callback.onManagePlugins();
			}
		};
		addLink(header, pluginsListener, "Plugins", "Manage plugins", PLUGINS.icon(baseUrl));

		// Import/Export link
		Button.ClickListener importExportListener = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				callback.onImportExport();
			}
		};
		addLink(header, importExportListener, "Import/Export", "Import and export simulation configuration", IMPORTEXPORT.icon(baseUrl));
		
		// Settings link
		Button.ClickListener settingsListener = new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				showSettingsDialog(settingsProvider, settingsChangeListener, versionInformation);
			}
		};
		addLink(header, settingsListener, "Settings", "Edit settings", SETTINGS.icon(baseUrl));
		
		// Add header to layout
		addComponent(header);

		tabSheet = createTabSheet();
		addComponent(tabSheet);
		setExpandRatio(tabSheet, 1.0f);
	}
	
	public void addTab(Component component, Resource icon, Refreshable refreshListener) {
		tabSheet.addTab(component, 0);
		tabSheet.getTab(component).setIcon(icon);
		tabSheet.setSelectedTab(component);
		if (refreshListener != null) {
			refreshMap.put(component, refreshListener);
		}
	}

	private void addLink(HorizontalLayout header, Button.ClickListener listener, String caption, String description, Resource icon) {
		Button button = createImageLink(caption, description, icon);
		button.addListener(listener);
		header.addComponent(button);
		header.setComponentAlignment(button, Alignment.TOP_RIGHT);
	}

	private static Button createImageLink(String caption, String description, Resource icon) {
		Button imageLinkButton = new Button(caption);
        imageLinkButton.setStyleName(BaseTheme.BUTTON_LINK);
        imageLinkButton.setDescription(description);
        imageLinkButton.setIcon(icon);
		return imageLinkButton;
	}

	private void showSettingsDialog(final SettingsProvider settingsProvider, final SettingsChangeListener settingsChangeListener, String versionInformation) {
		Settings settings = settingsProvider.getSettings();
		final Settings oldSettings = settings.clone();
		SettingsDialog.showModal(getWindow(), settings, new UpdateListener() {
			@Override public void notifyUpdated(Object obj) {
				Settings settings = (Settings) obj;
				settingsProvider.notifyChanged(settings);
				settingsChangeListener.settingsChanged(oldSettings, settings);
			}
		}, versionInformation);
	}

	private TabSheet createTabSheet() {				
		// Tabsheet
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		tabSheet.setWidth("100%");

		// Refresh view contents on tab selection
		tabSheet.addListener(createTabSelectionListener());

		return tabSheet;
	}

	private SelectedTabChangeListener createTabSelectionListener() {
		return new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = event.getTabSheet();
				refreshTab(refreshMap, tabSheet);
			}
		};
	}

	private void refreshTab(final Map<Component, Refreshable> refreshMap, TabSheet tabSheet) {
		Component tabLayout = tabSheet.getSelectedTab();
		Refreshable refreshment = refreshMap.get(tabLayout);
		if (refreshment != null) {
			refreshment.onRefresh();
		}
	}
}