package masquerade.sim.app.ui;

import static masquerade.sim.app.ui.Icons.ARTIFACT;
import static masquerade.sim.app.ui.Icons.IMPORTEXPORT;
import static masquerade.sim.app.ui.Icons.PLUGINS;
import static masquerade.sim.app.ui.Icons.REQUEST_HISTORY;
import static masquerade.sim.app.ui.Icons.SETTINGS;
import static masquerade.sim.app.ui.Icons.STATUS;
import static masquerade.sim.app.ui.Icons.TEST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.app.SendTestRequestAction;
import masquerade.sim.app.binding.ContainerFactory;
import masquerade.sim.app.binding.RequestHistoryContainerFactory;
import masquerade.sim.app.ui.view.FileManagerView;
import masquerade.sim.app.ui.view.RequestHistoryView;
import masquerade.sim.app.ui.view.RequestTestView;
import masquerade.sim.app.ui.view.StatusView;
import masquerade.sim.model.Settings;
import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.listener.UpdateListener;
import masquerade.sim.model.settings.SettingsProvider;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.WindowUtil;

import org.apache.commons.io.IOUtils;
import org.vaadin.codemirror2.client.ui.CodeMode;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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
public class MainLayout extends VerticalLayout {

	private static final boolean RESPONSE = false;

	private static final boolean REQUEST = true;

	private RequestTestView requestTestView;
	private RequestHistoryView requestHistoryView;

	private StatusView statusView;

	private TabSheet tabSheet;

	public MainLayout(Resource logo, RequestHistory requestHistory, File artifactRoot,
			SendTestRequestAction sendTestRequestAction, final SettingsChangeListener settingsChangeListener, String baseUrl, 
			final PluginManager pluginManager, final SettingsProvider settingsProvider, final String versionInformation) {
		
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
				showPluginsDialog(pluginManager);
			}
		};
		addLink(header, pluginsListener, "Plugins", "Manage plugins", PLUGINS.icon(baseUrl));

		// Import/Export link
		Button.ClickListener importExportListener = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO
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

		tabSheet = createTabSheet(requestHistory, artifactRoot, sendTestRequestAction, baseUrl);
		addComponent(tabSheet);
		setExpandRatio(tabSheet, 1.0f);
	}
	
	public void addTab(Component component, Resource icon) {
		tabSheet.addTab(component, 0);
		tabSheet.getTab(component).setIcon(icon);
		tabSheet.setSelectedTab(component);
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

	private void showPluginsDialog(PluginManager pluginManager) {
		PluginDialog.showModal(getWindow(), pluginManager);
	}

	private TabSheet createTabSheet(RequestHistory requestHistory, File artifactRoot, SendTestRequestAction sendTestRequestAction, String baseUrl) {
				
		Component fileManager = createFileManager(artifactRoot);
		Component requestTester = createRequestTestView(sendTestRequestAction);
		Component requestHistoryUi = createRequestHistoryView(requestHistory);
		Component status = createStatusView();

		// Tabsheet
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		tabSheet.setWidth("100%");

		// Add tabs
		tabSheet.addTab(fileManager, "Files", ARTIFACT.icon(baseUrl));
		tabSheet.addTab(requestTester, "Test", TEST.icon(baseUrl));
		tabSheet.addTab(requestHistoryUi, "History", REQUEST_HISTORY.icon(baseUrl));
		tabSheet.addTab(status, "Log", STATUS.icon(baseUrl));

		// Refresh view contents on tab selection
		Map<Component, RefreshListener> refreshMap = new HashMap<Component, RefreshListener>();
		refreshMap.put(requestTester, createTestRefresher());
		refreshMap.put(requestHistoryUi, createHistoryRefresher(requestHistory));
		refreshMap.put(status, createStatusViewRefresher());
		tabSheet.addListener(createTabSelectionListener(refreshMap));

		return tabSheet;
	}

	private RefreshListener createTestRefresher() {
		return new RefreshListener() {
			@Override
			public void refresh() {
				// TODO Auto-generated method stub
			}
		};
	}

	private RefreshListener createStatusViewRefresher() {
		return new RefreshListener() {
			@Override public void refresh() {
				statusView.refresh(StatusLogger.REPOSITORY.latestStatusLogs());
			}
		};
	}

	private Component createStatusView() {
		statusView = new StatusView();
		statusView.setSizeFull();
		statusView.setMargin(true);
		statusView.setSizeFull();
		
		statusView.addRefreshListener(new ClickListener() {
			private RefreshListener refresher = createStatusViewRefresher();
			@Override public void buttonClick(ClickEvent event) {
				refresher.refresh();
			}
		});
		
		statusView.addClearListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				StatusLogger.REPOSITORY.clear();
				statusView.refresh(StatusLogger.REPOSITORY.latestStatusLogs());
			}
		});
		
		return statusView;
	}

	private RefreshListener createHistoryRefresher(RequestHistory requestHistory) {
		final ContainerFactory history = new RequestHistoryContainerFactory(requestHistory);
		return new RefreshListener() {
			@Override public void refresh() {
				requestHistoryView.refresh((Filterable) history.createContainer());
			}
		};
	}

	private Component createRequestTestView(SendTestRequestAction sendTestRequestAction) {
		requestTestView = new RequestTestView(sendTestRequestAction);
		requestTestView.setMargin(true);
		return requestTestView;
	}

	private Component createFileManager(File artifactRoot) {
		return new FileManagerView(artifactRoot);
	}

	private SelectedTabChangeListener createTabSelectionListener(final Map<Component, RefreshListener> refreshMap) {
		return new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = event.getTabSheet();
				refreshTab(refreshMap, tabSheet);
			}
		};
	}

	private void refreshTab(final Map<Component, RefreshListener> refreshMap, TabSheet tabSheet) {
		Component tabLayout = tabSheet.getSelectedTab();
		RefreshListener refreshment = refreshMap.get(tabLayout);
		if (refreshment != null) {
			refreshment.refresh();
		}
	}

	private Component createRequestHistoryView(final RequestHistory requestHistory) {
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		// Log table
		requestHistoryView = new RequestHistoryView();
		requestHistoryView.setMargin(true);
		requestHistoryView.setSizeFull();
		layout.addComponent(requestHistoryView);
		layout.setExpandRatio(requestHistoryView, 1.0f);

		final RefreshListener refresher = createHistoryRefresher(requestHistory);
		refresher.refresh();

		// Show request details on double click
		requestHistoryView.addItemClickListener(new ItemClickListener() {
			@Override public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					showHistoryContent(((HistoryEntry) event.getItemId()), REQUEST);
				}
			}
		});

		// Button layout container
		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setMargin(false, true, true, true);
		bottomLayout.setSpacing(true);
		bottomLayout.setWidth("100%");
		bottomLayout.setSpacing(true);
		
		// Refresh button
		Button refreshButton = new Button("Refresh");
		refreshButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				refresher.refresh();
			}
		});
		bottomLayout.addComponent(refreshButton);

		// Clear button
		Button clearButton = new Button("Clear");
		clearButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				requestHistory.clear();
				refresher.refresh();
			}
		});
		bottomLayout.addComponent(clearButton);

		// Spacer
		Label spacer = new Label("Double-click on history entry to show details");
		bottomLayout.addComponent(spacer);
		bottomLayout.setExpandRatio(spacer, 1.0f);

		// Show request button
		final Button requestButton = new Button("Show Request");
		requestButton.setEnabled(false);
		requestButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				HistoryEntry entry = requestHistoryView.getSelection();
				showHistoryContent(entry, REQUEST);
			}
		});
		bottomLayout.addComponent(requestButton);

		// Show response button
		final Button responseButton = new Button("Show Response");
		responseButton.setEnabled(false);
		responseButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				HistoryEntry entry = requestHistoryView.getSelection();
				showHistoryContent(entry, RESPONSE);
			}
		});
		bottomLayout.addComponent(responseButton);
		
		// Enable/disable buttons upon selection
		requestHistoryView.addValueChangeListener(new ValueChangeListener() {
			@Override public void valueChange(ValueChangeEvent event) {
				boolean enabled = event.getProperty().getValue() != null;
				responseButton.setEnabled(enabled);
				requestButton.setEnabled(enabled);
			}
		});
		
		layout.addComponent(bottomLayout);

		return layout;
	}

	private void showHistoryContent(HistoryEntry historyEntry, boolean isRequest) {
		if (historyEntry != null) {
			String content;
			try {
				InputStream stream = isRequest ? historyEntry.readRequestData() : historyEntry.readResponseData();
				content = IOUtils.toString(stream);
				String title = (isRequest ? "Request" : "Response") + " Viewer";
				SourceViewWindow.showModal(getWindow(), title, content, CodeMode.XML);
			} catch (IOException e) {
				WindowUtil.showErrorNotification(getWindow(), "Error retrieving content", "Unable to retrieve request: " + e.getMessage());
			}
		}
	}
}