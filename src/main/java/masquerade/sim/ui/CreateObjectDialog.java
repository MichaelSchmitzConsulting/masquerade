package masquerade.sim.ui;

import java.lang.reflect.Constructor;
import java.util.Collection;

import masquerade.sim.CreateListener;
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

public class CreateObjectDialog extends Window {
	
	private static final String COMPONENT_WIDTH = "250px";

	public static void showModal(Window parent, String caption, String defaultName, final CreateListener listener, Collection<Class<?>> instanceTypes) {
		if (instanceTypes.isEmpty()) {
			throw new IllegalArgumentException("Expected at least one instance type to select from");
		}
		
		CreateObjectDialog dialog = new CreateObjectDialog(caption, defaultName, listener, instanceTypes);
		WindowUtil.getRoot(parent).addWindow(dialog);
	}
	
	private CreateObjectDialog(String caption, String defaultName, final CreateListener listener, Collection<Class<?>> instanceTypes) {
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
		
		// Type selection radios
		final Select select = new Select("Type");
		select.setNewItemsAllowed(false);
		select.setRequired(true);
		addComponent(select);
		
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
					getWindow().showNotification("Please specifiy a name");
				} else {
					Class<?> type = (Class<?>) select.getValue();
					doCreate(getWindow(), type, listener, name);
					WindowUtil.getRoot(getWindow()).removeWindow(CreateObjectDialog.this);
				}
			}
		});
		
		// The components added to the window are actually added to the window's
		// layout; you can use either. Alignments are set using the layout
		layout.addComponent(createButton);
		layout.setComponentAlignment(createButton, Alignment.TOP_RIGHT);
	}	

	// TODO: Refactor out of dialog
	private static void doCreate(Window window, Class<?> type, CreateListener listener, String name) {
		String typeName = ClassUtil.unqualifiedName(type);
		try {
			// By convention, a constructor with a single String argument is the construct that accepts an object name. 
			Constructor<?> constructor;
			try {
				constructor = type.getConstructor(String.class);
			} catch (NoSuchMethodException ex) {
				notifyError(window,  
					"No possible constructor found to create a " + typeName + ", expected " + typeName + "(String name) or " + typeName + "()",
					typeName);
				return;
			}
			Object value = constructor.newInstance(name);
			listener.notifyCreate(value);
		} catch (Exception e) {
			String exMessage = e.getMessage();
			String errMsg = e.getClass().getName() + " " + (exMessage == null ? "" : exMessage);
			notifyError(window, errMsg, typeName);
		}
	}
	
	private static void notifyError(Window window, String msg, String typeName) {
		WindowUtil.getRoot(window).showNotification(
             "Unable to create " + typeName,
             "<br/>" + msg,
             Notification.TYPE_ERROR_MESSAGE);
	}
}
