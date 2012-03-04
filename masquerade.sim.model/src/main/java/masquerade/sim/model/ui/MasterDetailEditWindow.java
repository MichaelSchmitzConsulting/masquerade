package masquerade.sim.model.ui;

import java.util.Collection;

import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.listener.SaveListener;
import masquerade.sim.model.listener.UpdateListener;
import masquerade.sim.model.ui.MasterDetailView.AddListener;
import masquerade.sim.util.AlwaysApprover;
import masquerade.sim.util.ClassUtil;
import masquerade.sim.util.ContainerUtil;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class MasterDetailEditWindow extends Window {
	private static final String[] COLUMNS = new String[] { "name" };
	
	private final Property property;
	private final MasterDetailView masterDetailView;
	private final SaveListener saveListener;

	public MasterDetailEditWindow(String caption, Property property, FormFieldFactory fieldFactory, Class<?> containedType, InstanceTypeProvider instanceTypes, SaveListener saveListener, boolean isAllowItemReordering) {
		super(caption);
		setModal(true);
		setWidth("800px");
		
		this.saveListener = saveListener;
		
		if (!Collection.class.isAssignableFrom(property.getType())) {
			throw new IllegalArgumentException("Dialog can only handle Collection item types, received " + property.getType().getName());
		}
		
		Collection<?> value = (Collection<?>) property.getValue();
		BeanItemContainer<?> container = ContainerUtil.collectionContainer(value, containedType);

		// Configure the windws layout; by default a VerticalLayout
		VerticalLayout layout = (VerticalLayout) getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		masterDetailView = new MasterDetailView(fieldFactory, isAllowItemReordering);
		masterDetailView.setDataSource(container);
		masterDetailView.setVisibleColumns(COLUMNS);
        masterDetailView.addFormCommitListener(createUpdateListener(masterDetailView));
        masterDetailView.addDeleteListener(createDeleteListener(masterDetailView));
        masterDetailView.addAddListener(createAddListener(containedType, masterDetailView, container, instanceTypes));
        masterDetailView.setWriteThrough(true); // Write directly to underlying bean item container, no save button (ok closes the dialog and will save the property value)
        masterDetailView.setSizeFull();
        masterDetailView.setMasterTableWidth("230px");
		layout.addComponent(masterDetailView);
		layout.setExpandRatio(masterDetailView, 1.0f);
		
		// Close button
		Button close = new Button("Save", new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				onSave();
			}
		});

		// The components added to the window are actually added to the window's
		// layout; you can use either. Alignments are set using the layout
		layout.addComponent(close);
		layout.setComponentAlignment(close, Alignment.TOP_RIGHT);
		
		this.property = property;
	}
	
	private AddListener createAddListener(final Class<?> containedType, final MasterDetailView view, final BeanItemContainer<?> container, final InstanceTypeProvider instanceTypes) {
		return new AddListener() {
			@Override
			public void onAdd() {
				String caption = "Add " + ClassUtil.fromCamelCase(containedType);
				String name = containedType.getSimpleName();
				if (name.length() > 1) {
					name = name.substring(0, 1).toLowerCase() + name.substring(1);
				}
				CreateObjectDialog.showModal(getWindow(), caption, name, objectCreatedListener(view, container), new AlwaysApprover(), instanceTypes);
			}
		};
	}

	private CreateListener objectCreatedListener(final MasterDetailView view, final BeanItemContainer<?> container) {
		return new CreateListener() {
			@Override
			public void notifyCreate(Object value) {
				container.addItem(value);
				view.setDataSource(container);
				view.setSelection(value);
			}
		};
	}

	private DeleteListener createDeleteListener(final MasterDetailView view) {
		return new DeleteListener() {
			@Override
			public void notifyDelete(Object obj) {
				Container container = view.getDataSource();
				container.removeItem(obj);
				view.setDataSource(container);
			}
		};
	}
	
	private UpdateListener createUpdateListener(final MasterDetailView view) {
		return new UpdateListener() {
			@Override
			public void notifyUpdated(Object obj) {
				view.refreshViewFromDataSource();
			}
		};
	}

	private void onSave() {
		// close the window by removing it from the parent window
		WindowUtil.getRoot(getWindow()).removeWindow(this);

		BeanItemContainer<?> dataSource = (BeanItemContainer<?>) masterDetailView.getDataSource();
		
		// In a BeanItemContainer, beans == ids, thus getItemIds() returns all beans in the container 
		Collection<?> itemIds = dataSource.getItemIds();
		
		@SuppressWarnings("unchecked")
		Collection<Object> value = (Collection<Object>) property.getValue();
		value.clear();
		value.addAll(itemIds);
		property.setValue(value);
		
		if (saveListener != null) {
			saveListener.onSave(value);
		}
	}
}
