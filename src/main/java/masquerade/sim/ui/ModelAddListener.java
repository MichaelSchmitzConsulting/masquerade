package masquerade.sim.ui;

import java.util.Collection;

import masquerade.sim.CreateListener;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.ui.MasterDetailView.AddListener;
import masquerade.sim.util.ClassUtil;

import com.vaadin.data.Container;

public final class ModelAddListener implements AddListener {
	private final Collection<Class<?>> instanceTypes;
	private final Class<?> baseType;
	private final ModelRepository repo;
	private final MasterDetailView view;
	private final Container container;

	public ModelAddListener(Collection<Class<?>> instanceTypes, Class<?> baseType, ModelRepository repo, MasterDetailView view, Container container) {
		this.instanceTypes = instanceTypes;
		this.baseType = baseType;
		this.repo = repo;
		this.view = view;
		this.container = container;
	}

	@Override
	public void onAdd() {
		String caption = "Add " + ClassUtil.fromCamelCase(baseType);
		String name = ClassUtil.unqualifiedName(baseType);
		if (name.length() > 1) {
			name = name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		
		int i = 2;
		String usedName = name;
		while (!repo.getByName(baseType, usedName).isEmpty()) {
			usedName = name + (i++); 
		}
		
		CreateObjectDialog.showModal(view.getWindow(), caption, usedName, objectCreatedListener(view, container, repo), repo, instanceTypes);
	}
	
	CreateListener objectCreatedListener(final MasterDetailView view, final Container container, final ModelRepository repo) {
		return new CreateListener() {
			@Override
			public void notifyCreate(Object value) {
				repo.notifyCreate(value);
				container.addItem(value);
				view.setDataSource(container);
				view.setSelection(value);
			}
		};
	}
}