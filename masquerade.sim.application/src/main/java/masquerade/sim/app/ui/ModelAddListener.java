package masquerade.sim.app.ui;

import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.model.ui.CreateNamedObjectDialog;
import masquerade.sim.model.ui.InstanceTypeProvider;
import masquerade.sim.model.ui.MasterDetailView;
import masquerade.sim.model.ui.MasterDetailView.AddListener;
import masquerade.sim.util.ClassUtil;

import com.vaadin.data.Container;

/**
 * Listener showing a {@link CreateNamedObjectDialog} for the a model type, 
 * providing a list of model type implementation choices and 
 * notifiying a {@link CreateListener} upon object creation.
 * Queries a {@link CreateApprover} if the model object can be created
 * before instantiation.
 */
public final class ModelAddListener implements AddListener {
	private final InstanceTypeProvider instanceTypeProvider;
	private final Class<?> baseType;
	private final CreateListener createListener;
	private final CreateApprover createApprover;
	private final MasterDetailView view;

	public ModelAddListener(
			InstanceTypeProvider instanceTypeProvider,
			Class<?> baseType,
			CreateListener createListener,
			CreateApprover createApprover,
			MasterDetailView view) {
		this.instanceTypeProvider = instanceTypeProvider;
		this.baseType = baseType;
		this.createListener = createListener;
		this.createApprover = createApprover;
		this.view = view;
	}

	@Override
	public void onAdd() {
		String caption = "Add " + ClassUtil.fromCamelCase(baseType);
		String name = baseType.getSimpleName();
		if (name.length() > 1) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		
		int i = 2;
		String usedName = name;
		while (createApprover.isNameUsed(baseType, usedName)) {
			usedName = name + (i++); 
		}
		
		CreateNamedObjectDialog.showModal(view.getWindow(), caption, usedName, new CreateNotifier(), createApprover, instanceTypeProvider);
	}
	
	private class CreateNotifier implements CreateListener {
		@Override
		public void notifyCreate(Object value) {
			createListener.notifyCreate(value);
			Container container = view.getDataSource();
			container.addItem(value);
			view.setDataSource(container);
			view.setSelection(value);
		}
	}
}