package masquerade.sim.ui;

import static masquerade.sim.ui.Icons.ARTIFACT;
import static masquerade.sim.ui.Icons.CHANNELS;
import static masquerade.sim.ui.Icons.REQUEST_HISTORY;
import static masquerade.sim.ui.Icons.REQUEST_ID_PROVIDER;
import static masquerade.sim.ui.Icons.REQUEST_MAPPING;
import static masquerade.sim.ui.Icons.RESPONSE;
import static masquerade.sim.ui.Icons.SCRIPT;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.CreateListener;
import masquerade.sim.DeleteListener;
import masquerade.sim.UpdateListener;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.FileType;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.model.Script;
import masquerade.sim.ui.MasterDetailView.AddListener;
import masquerade.sim.ui.UploadFileWindow.UploadResultListener;
import masquerade.sim.util.ClassUtil;
import masquerade.sim.util.WindowUtil;

import org.apache.commons.io.IOUtils;

import com.vaadin.data.Container;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

public class MainLayout extends VerticalLayout {

	private static final String[] VISIBLE_FILE_COLS = new String[] {
		FilesystemContainer.PROPERTY_NAME, FilesystemContainer.PROPERTY_SIZE, FilesystemContainer.PROPERTY_LASTMODIFIED };
	private static final String[] COLUMNS = new String[] { "name", "description" };

	public MainLayout(ModelRepository modelRepository, RequestHistory requestHistory, File artifactRoot) {
    	setSizeFull();
    	setMargin(true);    	
    	
    	TabSheet tabSheet = createTabSheet(modelRepository, requestHistory, artifactRoot);
        addComponent(tabSheet);
    }

	private TabSheet createTabSheet(ModelRepository modelRepository, RequestHistory requestHistory, File artifactRoot) {
		// Container factories retrieving model objects from the model repository and packing
    	// them into a Container suitable for binding to a view.
        ContainerFactory channelFactory = new ModelContainerFactory(modelRepository, Channel.class);
        ContainerFactory mappingFactory = new ModelContainerFactory(modelRepository, RequestMapping.class);
        ContainerFactory responseFactory = new ModelContainerFactory(modelRepository, ResponseSimulation.class);
        ContainerFactory scriptFactory = new ModelContainerFactory(modelRepository, Script.class);
        ContainerFactory ripFactory = new ModelContainerFactory(modelRepository, RequestIdProvider.class);
                
        // Create tabs
        FormFieldFactory fieldFactory = new ModelFieldFactory(modelRepository);
		Component channels = createEditorTab(channelFactory, modelRepository, fieldFactory);
        Component requestMapping = createEditorTab(mappingFactory, modelRepository, fieldFactory);
        Component responseSim = createEditorTab(responseFactory, modelRepository, fieldFactory);
        Component scripts = createEditorTab(scriptFactory, modelRepository, fieldFactory);
        Component requestIdProviders = createEditorTab(ripFactory, modelRepository, fieldFactory);
        Component requestHistoryUi = createRequestHistoryView(requestHistory);
        Component artifactManager = createArtifactManager(artifactRoot);
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.setHeight("100%");
        tabSheet.setWidth("100%");
        
        //  Add tabs
        tabSheet.addTab(channels, "Channels", CHANNELS.icon());
        tabSheet.addTab(requestMapping, "Request Mapping", REQUEST_MAPPING.icon());
        tabSheet.addTab(responseSim, "Response Simulation", RESPONSE.icon());
        tabSheet.addTab(scripts, "Scripts", SCRIPT.icon());
        tabSheet.addTab(requestIdProviders, "Request ID Providers", REQUEST_ID_PROVIDER.icon());
        tabSheet.addTab(requestHistoryUi, "Request History", REQUEST_HISTORY.icon());
        tabSheet.addTab(artifactManager, "Artifacts", ARTIFACT.icon());
        
        // Refresh master/detail view contents on tab selection
        Map<Component, ContainerFactory> refreshMap = new HashMap<Component, ContainerFactory>();
        refreshMap.put(channels, channelFactory);
        refreshMap.put(requestMapping, mappingFactory);
        refreshMap.put(responseSim, responseFactory);
        refreshMap.put(scripts, scriptFactory);
        refreshMap.put(requestIdProviders, ripFactory);
        tabSheet.addListener(createTabSelectionListener(refreshMap));
        
		return tabSheet;
	}

	private Component createArtifactManager(File artifactRoot) {
		HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSizeFull();

        // Create file list view
		final MasterDetailView view = new MasterDetailView();
		layout.addComponent(view);

		// Add filesystem container to display templates
        final File templateRoot = new File(artifactRoot, FileType.TEMPLATE.name().toLowerCase());
        if (templateRoot.exists()) {
        	updateArtifactView(view, templateRoot);
        }
        
/*        view.addFormCommitListener(commit);
        view.addDeleteListener(del);*/
        final UploadResultListener uploadListener = new UploadResultListener() {
			@Override public void onUploadFailed() {
				// Ignored, upload window already shows error notification
			}
			@Override public void onUploadDone(File file) {
				updateArtifactView(view, templateRoot);
			}        	
        };
        
        view.addAddListener(new AddListener() {
			@Override public void onAdd() {
				UploadFileWindow.showModal(getWindow(), "Upload Artifact", templateRoot, uploadListener);
			}
		});
        
		return layout;
	}

	private void updateArtifactView(MasterDetailView view, final File templateRoot) {
		Container container = new FilesystemContainer(templateRoot);
		view.setDataSource(container, VISIBLE_FILE_COLS);
	}

	private SelectedTabChangeListener createTabSelectionListener(final Map<Component, ContainerFactory> refreshMap) {
		return new SelectedTabChangeListener() {
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
				TabSheet tabSheet = event.getTabSheet();
				refreshTab(refreshMap, tabSheet);
			}
		};
	}
	
	private void refreshTab(final Map<Component, ContainerFactory> refreshMap, TabSheet tabSheet) {
		Component tabLayout = tabSheet.getComponentIterator().next();
		ContainerFactory containerFactory = refreshMap.get(tabLayout);
		if (containerFactory != null) {
			ComponentContainer container = (ComponentContainer) tabLayout;
			MasterDetailView view = (MasterDetailView) container.getComponentIterator().next();
			Container dataSource = containerFactory.createContainer();
			view.setDataSource(dataSource, COLUMNS);
		}
	}

	private Component createEditorTab(ContainerFactory containerFactory, ModelRepository repo, FormFieldFactory fieldFactory) {
		Class<?> modelType = containerFactory.getType();
		Collection<Class<?>> instanceTypes = repo.getModelImplementations(modelType);
		
		Container container = containerFactory.createContainer();
		
	    VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        MasterDetailView view = new MasterDetailView(fieldFactory);
        view.setDataSource(container, COLUMNS);
        view.addFormCommitListener(repo);
        view.addFormCommitListener(createUpdateListener(view, container));
        view.addDeleteListener(repo);
        view.addDeleteListener(createDeleteListener(view, container));
        view.addAddListener(createAddListener(modelType, view, container, instanceTypes, repo));
        layout.addComponent(view);
        
	    return layout;
    }

	private Component createRequestHistoryView(final RequestHistory requestHistory) {
		final ContainerFactory history = new RequestHistoryContainerFactory(requestHistory);

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		
		// History view
		final RequestHistoryView view = new RequestHistoryView();
		view.refresh(history.createContainer());
		view.setMargin(true);
		view.setSizeFull();
		layout.addComponent(view);
		layout.setExpandRatio(view, 1.0f);
		
		// Show request details on double click
		view.addItemClickListener(new ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					showRequestContent((HistoryEntry) event.getItemId());
				}
			}
		});
				
		// Refresh button
		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setSpacing(true);
		Button refreshButton = new Button("Refresh");
		refreshButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				view.refresh(history.createContainer());
			}
		});
		bottomLayout.addComponent(refreshButton);
		
		// Clear button
		Button clearButton = new Button("Clear");
		clearButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				requestHistory.clear();
				view.refresh(history.createContainer());
			}
		});
		bottomLayout.addComponent(clearButton);

		// Help label
		Label label = new Label("Doubleclick on request to show payload");
		bottomLayout.addComponent(label);
		bottomLayout.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

		bottomLayout.setMargin(false, true, true, true);
		bottomLayout.setSpacing(true);
		layout.addComponent(bottomLayout);
		
		return layout;
	}
	
	private void showRequestContent(HistoryEntry historyEntry) {
		if (historyEntry != null) {
			String content;
			try {
				InputStream stream = historyEntry.readRequestData();
				content = IOUtils.toString(stream);
				SourceViewWindow.showModal(getWindow(), "Request Viewer", content);
			} catch (IOException e) {
				WindowUtil.showErrorNotification(getWindow(), "Error retrieving content", "Unable to retrieve request: " + e.getMessage());
			}
		}
	}

	private AddListener createAddListener(
			final Class<?> baseType,
			final MasterDetailView view, 
			final Container container, 
			final Collection<Class<?>> instanceTypes,
			final ModelRepository repo) {
		return new AddListener() {
			@Override public void onAdd() {
				String caption = "Add " + ClassUtil.fromCamelCase(baseType);
				String name = ClassUtil.unqualifiedName(baseType);
				if (name.length() > 1) {
					name = name.substring(0, 1).toLowerCase() + name.substring(1);
				}
				CreateObjectDialog.showModal(getWindow(), caption, name, objectCreatedListener(view, container, repo), instanceTypes);
			}
		};
	}
	
	private CreateListener objectCreatedListener(final MasterDetailView view, final Container container, final ModelRepository repo) {
		return new CreateListener() {
			@Override public void notifyCreate(Object value) {
				repo.notifyCreate(value);
				container.addItem(value);
				view.setDataSource(container, COLUMNS);
				view.setSelection(value);
			}
		};
	}
	
	private DeleteListener createDeleteListener(final MasterDetailView view, final Container container) {
		return new DeleteListener() {
			@Override
			public void notifyDelete(Object obj) {
				container.removeItem(obj);
				view.setDataSource(container, COLUMNS);
			}
		};
	}

	private UpdateListener createUpdateListener(final MasterDetailView view, final Container container) {
		return new UpdateListener() { 
			@Override public void notifyUpdated(Object obj) {
				view.setDataSource(container, COLUMNS);
			}
		};
    }
}