package masquerade.sim.ui;

import static masquerade.sim.ui.Icons.ARTIFACT;
import static masquerade.sim.ui.Icons.CHANNELS;
import static masquerade.sim.ui.Icons.IMPORTEXPORT;
import static masquerade.sim.ui.Icons.NAMESPACE_PREFIX;
import static masquerade.sim.ui.Icons.REQUEST_HISTORY;
import static masquerade.sim.ui.Icons.REQUEST_ID_PROVIDER;
import static masquerade.sim.ui.Icons.REQUEST_MAPPING;
import static masquerade.sim.ui.Icons.SCRIPT;
import static masquerade.sim.ui.Icons.SETTINGS;
import static masquerade.sim.ui.Icons.STATUS;
import static masquerade.sim.ui.Icons.TEST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.DeleteListener;
import masquerade.sim.SettingsChangeListener;
import masquerade.sim.UpdateListener;
import masquerade.sim.app.UploadHandler;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.NamespacePrefix;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Settings;
import masquerade.sim.model.impl.FileLoaderImpl;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.ui.MasterDetailView.AddListener;
import masquerade.sim.util.WindowUtil;

import org.apache.commons.io.IOUtils;
import org.vaadin.codemirror.client.ui.CodeStyle;

import com.vaadin.data.Container;
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
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormFieldFactory;
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
public class MainLayout extends VerticalLayout {

	private static final boolean RESPONSE = false;

	private static final boolean REQUEST = true;

	/** Columns shown in model master/detail views */
	private static final String[] COLUMNS = new String[] { "name", "description" };

	private RequestTestView requestTestView;
	private RequestHistoryView requestHistoryView;

	private StatusView statusView;

	public MainLayout(Resource logo, final ModelRepository modelRepository, RequestHistory requestHistory, File artifactRoot,
			ActionListener<Channel, String, Object> sendTestRequestAction, final SettingsChangeListener settingsChangeListener, String baseUrl,
			final UploadHandler modelUploadHandler, final DownloadHandler downloadHandler) {
		
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
		
		// Import/Export link
		Button importExportButton = createImageLink("Import/Export", "Import and export simulation configuration", IMPORTEXPORT.icon(baseUrl));
		importExportButton.addListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				showImportExportDialog(modelRepository, modelUploadHandler, downloadHandler);
			}
		});
		header.addComponent(importExportButton);
		header.setComponentAlignment(importExportButton, Alignment.TOP_RIGHT);
		
		// Settings link
		Button settingsButton = createImageLink("Settings", "Edit settings", SETTINGS.icon(baseUrl));
        settingsButton.addListener(new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				showSettingsDialog(modelRepository, settingsChangeListener);
			}
		});
		header.addComponent(settingsButton);
		header.setComponentAlignment(settingsButton, Alignment.TOP_RIGHT);
		
		addComponent(header);

		// Main tab layout
		TabSheet tabSheet = createTabSheet(modelRepository, requestHistory, artifactRoot, sendTestRequestAction, baseUrl);
		addComponent(tabSheet);
		setExpandRatio(tabSheet, 1.0f);
	}

	private void showImportExportDialog(ModelRepository modelRepository, UploadHandler uploadHandler, DownloadHandler downloadHandler) {
		ImportExportDialog.showModal(getWindow(), uploadHandler, downloadHandler);
	}

	private static Button createImageLink(String caption, String description, Resource icon) {
		Button imageLinkButton = new Button(caption);
        imageLinkButton.setStyleName(BaseTheme.BUTTON_LINK);
        imageLinkButton.setDescription(description);
        imageLinkButton.setIcon(icon);
		return imageLinkButton;
	}

	private void showSettingsDialog(final ModelRepository modelRepository, final SettingsChangeListener settingsChangeListener) {
		Settings settings = modelRepository.getSettings();
		final Settings oldSettings = settings.clone();
		SettingsDialog.showModal(getWindow(), settings, new UpdateListener() {
			@Override public void notifyUpdated(final Object obj) {
				modelRepository.notifyUpdated(obj);
				settingsChangeListener.settingsChanged(oldSettings, (Settings) obj);
			}
		});
	}

	private TabSheet createTabSheet(ModelRepository modelRepository, RequestHistory requestHistory, File artifactRoot,
			ActionListener<Channel, String, Object> sendTestRequestAction, String baseUrl) {
		// Container factories retrieving model objects from the model
		// repository and packing them into a Container suitable for binding to a view.
		ContainerFactory channelFactory = new ModelContainerFactory(modelRepository, Channel.class);
		ContainerFactory mappingFactory = new ModelContainerFactory(modelRepository, RequestMapping.class);
		ContainerFactory scriptFactory = new ModelContainerFactory(modelRepository, Script.class);
		ContainerFactory namespacePrefixFactory = new ModelContainerFactory(modelRepository, NamespacePrefix.class);
		ContainerFactory ripFactory = new ModelContainerFactory(modelRepository, RequestIdProvider.class);
		FileLoader fileLoader = new FileLoaderImpl(artifactRoot);

		// Create tabs
		FormFieldFactory fieldFactory = new ModelFieldFactory(modelRepository, fileLoader);
		ComponentContainer channels = createEditorTab(channelFactory, modelRepository, fieldFactory);
		ComponentContainer scripts = createEditorTab(scriptFactory, modelRepository, fieldFactory);
		ComponentContainer requestMapping = createEditorTab(mappingFactory, modelRepository, fieldFactory);
		ComponentContainer requestIdProviders = createEditorTab(ripFactory, modelRepository, fieldFactory);
		ComponentContainer namespacePrefixes = createEditorTab(namespacePrefixFactory, modelRepository, fieldFactory);
		Component fileManager = createFileManager(artifactRoot);
		Component requestTester = createRequestTestView(modelRepository, sendTestRequestAction);
		Component requestHistoryUi = createRequestHistoryView(requestHistory);
		Component status = createStatusView();

		// Tabsheet
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeight("100%");
		tabSheet.setWidth("100%");

		// Add tabs
		tabSheet.addTab(channels, "Channel", CHANNELS.icon(baseUrl));
		tabSheet.addTab(scripts, "Response Script", SCRIPT.icon(baseUrl));
		tabSheet.addTab(requestMapping, "Request Mapping", REQUEST_MAPPING.icon(baseUrl));
		tabSheet.addTab(requestIdProviders, "Request ID Provider", REQUEST_ID_PROVIDER.icon(baseUrl));
		tabSheet.addTab(namespacePrefixes, "Namespace", NAMESPACE_PREFIX.icon(baseUrl));
		tabSheet.addTab(fileManager, "Artifacts", ARTIFACT.icon(baseUrl));
		tabSheet.addTab(requestTester, "Test", TEST.icon(baseUrl));
		tabSheet.addTab(requestHistoryUi, "History", REQUEST_HISTORY.icon(baseUrl));
		tabSheet.addTab(status, "Status", STATUS.icon(baseUrl));

		// Refresh view contents on tab selection
		Map<Component, RefreshListener> refreshMap = new HashMap<Component, RefreshListener>();
		refreshMap.put(channels, new TabRefresher(channelFactory, channels));
		refreshMap.put(requestMapping, new TabRefresher(mappingFactory,requestMapping));
		refreshMap.put(scripts, new TabRefresher(scriptFactory, scripts));
		refreshMap.put(requestIdProviders, new TabRefresher(ripFactory, requestIdProviders));
		refreshMap.put(requestTester, createTestRefresher(modelRepository));
		refreshMap.put(requestHistoryUi, createHistoryRefresher(requestHistory));
		refreshMap.put(status, createStatusViewRefresher());
		tabSheet.addListener(createTabSelectionListener(refreshMap));

		return tabSheet;
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
				requestHistoryView.refresh(history.createContainer());
			}
		};
	}

	private RefreshListener createTestRefresher(final ModelRepository modelRepository) {
		return new RefreshListener() {
			@Override public void refresh() {
				updateTestView(modelRepository);
			}
		};
	}

	private Component createRequestTestView(ModelRepository modelRepository, ActionListener<Channel, String, Object> sendTestRequestAction) {
		requestTestView = new RequestTestView(sendTestRequestAction);
		updateTestView(modelRepository);
		requestTestView.setMargin(true);
		return requestTestView;
	}

	private void updateTestView(ModelRepository modelRepository) {
		Collection<Channel> channels = modelRepository.getAll(Channel.class);
		requestTestView.setChannels(channels);
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

	private ComponentContainer createEditorTab(ContainerFactory containerFactory, ModelRepository repo, FormFieldFactory fieldFactory) {
		Class<?> modelType = containerFactory.getType();
		Collection<Class<?>> instanceTypes = repo.getModelImplementations(modelType);

		Container container = containerFactory.createContainer();

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		MasterDetailView view = new MasterDetailView(fieldFactory);
		view.setDataSource(container);
		view.setVisibleColumns(COLUMNS);
		view.addFormCommitListener(repo);
		view.addFormCommitListener(createUpdateListener(view, container));
		view.addDeleteListener(repo);
		view.addDeleteListener(createDeleteListener(view, container));
		view.addAddListener(createAddListener(modelType, view, container, instanceTypes, repo));
		view.setDeleteApprover(repo);
		layout.addComponent(view);

		return layout;
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
		Label spacer = new Label();
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
				SourceViewWindow.showModal(getWindow(), title, content, CodeStyle.XML);
			} catch (IOException e) {
				WindowUtil.showErrorNotification(getWindow(), "Error retrieving content", "Unable to retrieve request: " + e.getMessage());
			}
		}
	}

	private AddListener createAddListener(final Class<?> baseType, final MasterDetailView view, final Container container,
			final Collection<Class<?>> instanceTypes, final ModelRepository repo) {
		return new ModelAddListener(instanceTypes, baseType, repo, view, container);
	}

	private DeleteListener createDeleteListener(final MasterDetailView view, final Container container) {
		return new DeleteListener() {
			@Override public void notifyDelete(Object obj) {
				container.removeItem(obj);
				view.setDataSource(container);
			}
		};
	}

	private UpdateListener createUpdateListener(final MasterDetailView view, final Container container) {
		return new UpdateListener() {
			@Override public void notifyUpdated(Object obj) {
				view.setDataSource(container);
			}
		};
	}
}