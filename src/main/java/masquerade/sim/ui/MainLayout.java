package masquerade.sim.ui;

import static masquerade.sim.ui.Icons.CHANNELS;
import static masquerade.sim.ui.Icons.REQUEST_HISTORY;
import static masquerade.sim.ui.Icons.REQUEST_ID_PROVIDER;
import static masquerade.sim.ui.Icons.REQUEST_MAPPING;
import static masquerade.sim.ui.Icons.RESPONSE;
import static masquerade.sim.ui.Icons.SCRIPT;

import java.util.Collection;

import masquerade.sim.CreateListener;
import masquerade.sim.DeleteListener;
import masquerade.sim.UpdateListener;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.model.Script;
import masquerade.sim.ui.MasterDetailView.AddListener;
import masquerade.sim.util.ClassUtil;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

public class MainLayout extends VerticalLayout {

    public MainLayout(ModelRepository modelRepository, RequestHistory requestHistory) {
    	setSizeFull();
    	setMargin(true);
    	
    	FormFieldFactory fieldFactory = new ModelFieldFactory(modelRepository);
    	
    	// Create tabs
        Component channels = createEditorTab(container(modelRepository.getChannels(), Channel.class), modelRepository, fieldFactory);
        Component requestMapping = createEditorTab(container(modelRepository.getRequestMappings(), RequestMapping.class), modelRepository, fieldFactory);
        Component responseSim = createEditorTab(container(modelRepository.getResponseSimulations(), ResponseSimulation.class), modelRepository, fieldFactory);
        Component scripts = createEditorTab(container(modelRepository.getScripts(), Script.class), modelRepository, fieldFactory);
        Component requestIdProviders = createEditorTab(container(modelRepository.getRequestIdProviders(), RequestIdProvider.class), modelRepository, fieldFactory);
        
        Component requestHistoryUi = createRequestHistory(requestHistory);
        
        TabSheet tabSheet = new TabSheet();
        tabSheet.setHeight("100%");
        tabSheet.setWidth("100%");
        
        tabSheet.addTab(channels, "Channels", CHANNELS.icon());
        tabSheet.addTab(requestMapping, "Request Mapping", REQUEST_MAPPING.icon());
        tabSheet.addTab(responseSim, "Response Simulation", RESPONSE.icon());
        tabSheet.addTab(scripts, "Scripts", SCRIPT.icon());
        tabSheet.addTab(requestIdProviders, "Request ID Providers", REQUEST_ID_PROVIDER.icon());
        tabSheet.addTab(requestHistoryUi, "Request History", REQUEST_HISTORY.icon());
        
        addComponent(tabSheet);
    }

	private Component createEditorTab(BeanItemContainer<?> container, ModelRepository repo, FormFieldFactory fieldFactory) {
		Class<?> modelType = container.getBeanType();
		Collection<Class<?>> instanceTypes = repo.getModelImplementations(modelType);
		
	    VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeFull();
        MasterDetailView view = new MasterDetailView(fieldFactory);
        String[] visibleColumns = new String[] { "name", "description" };
		view.setDataSource(container, visibleColumns);
        view.addFormCommitListener(repo);
        view.addFormCommitListener(createUpdateListener(view, container, visibleColumns));
        view.addDeleteListener(repo);
        view.addDeleteListener(createDeleteListener(view, container, visibleColumns));
        view.addAddListener(createAddListener(modelType, view, container, visibleColumns, instanceTypes, repo));
        layout.addComponent(view);
        
	    return layout;
    }

	private RequestHistoryView createRequestHistory(RequestHistory requestHistory) {
		RequestHistoryView view = new RequestHistoryView();
		view.refresh(requestHistory);
		return view;
	}

	private AddListener createAddListener(
			final Class<?> baseType,
			final MasterDetailView view, 
			final BeanItemContainer<?> container, 
			final String[] visibleColumns,
			final Collection<Class<?>> instanceTypes,
			final ModelRepository repo) {
		return new AddListener() {
			@Override public void onAdd() {
				String caption = "Add " + ClassUtil.fromCamelCase(baseType);
				String name = ClassUtil.unqualifiedName(baseType);
				if (name.length() > 1) {
					name = name.substring(0, 1).toLowerCase() + name.substring(1);
				}
				CreateObjectDialog.showModal(getWindow(), caption, name, objectCreatedListener(view, container, visibleColumns, repo), instanceTypes);
			}
		};
	}
	
	private CreateListener objectCreatedListener(final MasterDetailView view, final BeanItemContainer<?> container, final String[] visibleColumns, final ModelRepository repo) {
		return new CreateListener() {
			@Override public void notifyCreate(Object value) {
				repo.notifyCreate(value);
				container.addItem(value);
				view.setDataSource(container, visibleColumns);
				view.setSelection(value);
			}
		};
	}
	
	private DeleteListener createDeleteListener(final MasterDetailView view, final BeanItemContainer<?> container, final String[] visibleColumns) {
		return new DeleteListener() {
			@Override
			public void notifyDelete(Object obj) {
				container.removeItem(obj);
				view.setDataSource(container, visibleColumns);
			}
		};
	}

	private UpdateListener createUpdateListener(final MasterDetailView view, final BeanItemContainer<?> container, final String[] visibleColumns) {
		return new UpdateListener() { 
			@Override public void notifyUpdated(Object obj) {
				view.setDataSource(container, visibleColumns);
			}
		};
    }

	private static <T> BeanItemContainer<T> container(Collection<T> collection, Class<?> type) {
		@SuppressWarnings("unchecked") // Makes it possible to pass List<X<?>>, X.class into container()
        Class<T> cast = (Class<T>) type;
		return new BeanItemContainer<T>(cast, collection);
	}
}