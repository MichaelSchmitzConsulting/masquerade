package masquerade.sim.model.ui;

import java.util.Collection;

import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.util.ClassUtil;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CreateNamedObjectDialog extends Window {
	
	private static final String COMPONENT_WIDTH = "250px";

	public static void showModal(Window parent, String caption, String defaultName, CreateListener listener, CreateApprover approver, InstanceTypeProvider instanceTypes) {
		CreateNamedObjectDialog dialog = new CreateNamedObjectDialog(caption, defaultName, listener, approver, instanceTypes);
		WindowUtil.getRoot(parent).addWindow(dialog);
	}
	
	private CreateNamedObjectDialog(String caption, String defaultName, final CreateListener listener, final CreateApprover approver, InstanceTypeProvider instanceTypeProvider) {
		super(caption);
		
		setModal(true);
		setResizable(false);
		
		// Configure the windws layout; by default a VerticalLayout
		VerticalLayout layout = (VerticalLayout) getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined();
		
		// Name text field
		final TextField nameText = new TextField();
		nameText.setValue(defaultName);
		nameText.setWidth(COMPONENT_WIDTH);
		addComponent(nameText);
		
		// Type selection dropdown
		final Select select = new Select("Type");
		select.setNewItemsAllowed(false);
		select.setRequired(true);
		addComponent(select);
		
        Collection<Class<?>> instanceTypes = instanceTypeProvider.getInstanceTypes();
        
		@SuppressWarnings({ "unchecked", "rawtypes" })
		BeanItemContainer<?> container = new BeanItemContainer(Class.class, instanceTypes);
		select.setContainerDataSource(container);
		for (Class<?> item : instanceTypes) {
			select.setItemCaption(item, ClassUtil.fromCamelCase(item));
		}
		select.select(instanceTypes.iterator().next());
		select.setWidth(COMPONENT_WIDTH);
		
		// Create button
		Button createButton = new Button("Create", new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				String name = (String) nameText.getValue();
				if (name.length() == 0) {
					getWindow().showNotification("Please specify a name");
				} else {
					Class<?> type = (Class<?>) select.getValue();
					if (!approver.isNameUsed(name)) {
						Object value = ModelObjectFactory.createNamedModelObject(getWindow(), type, name);
						listener.notifyCreate(value);
						WindowUtil.getRoot(getWindow()).removeWindow(CreateNamedObjectDialog.this);
					} else {
						WindowUtil.showErrorNotification(getWindow(), "Unable to create object", "Name is already taken, please choose a different name.");
					}
				}
			}
		});
		
		// The components added to the window are actually added to the window's
		// layout; you can use either. Alignments are set using the layout
		layout.addComponent(createButton);
		layout.setComponentAlignment(createButton, Alignment.TOP_RIGHT);
	}
}
