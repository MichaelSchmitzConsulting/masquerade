package masquerade.sim.model.ui;

import java.lang.reflect.Constructor;
import java.util.Collection;

import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
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
public class CreateObjectDialog extends Window {
	
	private static final String COMPONENT_WIDTH = "250px";
	private static final StatusLog log = StatusLogger.get(CreateObjectDialog.class);

	public static void showModal(Window parent, String caption, String defaultName, CreateListener listener, CreateApprover approver, InstanceTypeProvider instanceTypes) {
		CreateObjectDialog dialog = new CreateObjectDialog(caption, defaultName, listener, approver, instanceTypes);
		WindowUtil.getRoot(parent).addWindow(dialog);
	}
	
	private CreateObjectDialog(String caption, String defaultName, final CreateListener listener, final CreateApprover approver, InstanceTypeProvider instanceTypeProvider) {
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
		
        Collection<Class<?>> instanceTypes = instanceTypeProvider.getInstanceTypes();
        
		@SuppressWarnings({ "unchecked", "rawtypes" })
		BeanItemContainer<?> container = new BeanItemContainer(Class.class, instanceTypes );
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
					StringBuilder vetoMsg = new StringBuilder();
					if (approver.canCreate(type, name, vetoMsg)) {
						doCreate(getWindow(), type, listener, name);
						WindowUtil.getRoot(getWindow()).removeWindow(CreateObjectDialog.this);
					} else {
						WindowUtil.showErrorNotification(getWindow(), "Cannot create object", vetoMsg.toString());
					}
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
		String typeName = type.getSimpleName();
		try {
			// By convention, a constructor with a single String argument is the constructor that accepts an object name. 
			Constructor<?> constructor;
			try {
				constructor = type.getConstructor(String.class);
			} catch (NoSuchMethodException ex) {
				notifyError(window,  
					"No possible constructor found to create a " + typeName + ", expected " + typeName + "(String name) or " + typeName + "()",
					typeName, ex);
				return;
			}
			Object value = constructor.newInstance(name);
			listener.notifyCreate(value);
		} catch (Exception e) {
			String exMessage = e.getMessage();
			String errMsg = e.getClass().getName() + " " + (exMessage == null ? "" : exMessage);
			notifyError(window, errMsg, typeName, e);
		}
	}
	
	private static void notifyError(Window window, String msg, String typeName, Exception e) {
		String caption = "Unable to create " + typeName;
		log .error(caption + ": " + msg, e);
		
		WindowUtil.getRoot(window).showNotification(
             caption,
             "<br/>" + msg,
             Notification.TYPE_ERROR_MESSAGE);
	}
}
