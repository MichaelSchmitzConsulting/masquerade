package masquerade.sim.app.ui.view;

import masquerade.sim.app.binding.ContainerFactory;
import masquerade.sim.app.ui.ModelAddListener;
import masquerade.sim.model.ModelInstanceTypeProvider;
import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.model.listener.DeleteApprover;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.listener.UpdateListener;
import masquerade.sim.model.ui.InstanceTypeProvider;
import masquerade.sim.model.ui.MasterDetailView;
import masquerade.sim.model.ui.MasterDetailView.AddListener;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.data.Container;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.VerticalLayout;

/**
 * Default simulation model editor view, edits types with a generic master/detail view providing
 * add/delete/edit functionality for model types.
 */
@SuppressWarnings("serial")
public class SimulationTypeEditorView extends VerticalLayout {
	/** Columns shown in model master/detail views */
	private static final String[] COLUMNS = new String[] { "name", "description" };
	
	private final MasterDetailView masterDetailView;

	public SimulationTypeEditorView(ContainerFactory containerFactory, FormFieldFactory fieldFactory,
			UpdateListener formCommitListener, DeleteListener deleteListener, DeleteApprover deleteApprover,
			CreateListener createListener, CreateApprover createApprover, PluginRegistry pluginRegistry) {
		
		Class<?> modelType = containerFactory.getType();
		ModelInstanceTypeProvider instanceTypeProvider = new ModelInstanceTypeProvider(modelType, pluginRegistry);
		Container container = containerFactory.createContainer();

		setMargin(true);
		setSizeFull();
				
		masterDetailView = new MasterDetailView(fieldFactory);
		masterDetailView.setDataSource(container);
		masterDetailView.setVisibleColumns(COLUMNS);
		masterDetailView.addFormCommitListener(formCommitListener);
		masterDetailView.addFormCommitListener(new ViewUpdateListener());
		masterDetailView.addDeleteListener(deleteListener);
		masterDetailView.addDeleteListener(new ViewDeleteListener());
		
		AddListener modelAddListener = createAddListener(modelType, masterDetailView, instanceTypeProvider, createListener, createApprover);
		masterDetailView.addAddListener(modelAddListener);
		masterDetailView.setDeleteApprover(deleteApprover);
		addComponent(masterDetailView);		
	}

	private class ViewDeleteListener implements DeleteListener {
		@Override public void notifyDelete(Object obj) {
			Container container = masterDetailView.getDataSource();
			container.removeItem(obj);
			masterDetailView.setDataSource(container);
		}		
	}

	private class ViewUpdateListener implements UpdateListener {
		@Override public void notifyUpdated(Object obj) {
			masterDetailView.refreshViewFromDataSource();
		}		
	}
	
	private AddListener createAddListener(Class<?> baseType, MasterDetailView view, InstanceTypeProvider instanceTypes, 
			CreateListener createListener, CreateApprover createApprover) {
		return new ModelAddListener(instanceTypes, baseType, createListener, createApprover, view);
	}
}
