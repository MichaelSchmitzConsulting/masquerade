package masquerade.sim.model.ui;

import masquerade.sim.util.WindowUtil;

import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class EditWindowField extends CustomField {

	public interface WindowFactory {
		Window createWindow(Property property, Class<?> type, FormFieldFactory fieldFactory);
	}
	
	private Class<?> type;
	private FormFieldFactory fieldFactory;
	private Class<?> containedType;
	private InstanceTypeProvider instanceTypeProvider;
	private WindowFactory windowFactory;
	private HorizontalLayout mainLayout;
	private int positionX = -1;

	/**
	 * Creates a field with an 'Edit...' button that opens a modal MasterDetailEditWindow to edit a collection
	 * @param valueType
	 * @param containedType
	 * @param instanceTypes
	 * @param fieldFactory
	 * @param windowFactory
	 */
	public EditWindowField(Class<?> valueType, Class<?> containedType, InstanceTypeProvider instanceTypeProvider, FormFieldFactory fieldFactory, WindowFactory windowFactory) {
		this.type = valueType;
		this.instanceTypeProvider = instanceTypeProvider;
		this.containedType = containedType;
		this.fieldFactory = fieldFactory;
		this.windowFactory = windowFactory;
		
		mainLayout = new HorizontalLayout();
		mainLayout.setSpacing(true);

		final Button button = new Button("Edit...");
		button.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				editValue();
			}
		});
		mainLayout.addComponent(button);
		
		setCompositionRoot(mainLayout);
	}

	/**
	 * @param positionX Top position of the window. Centered if not specified.
	 */
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	private void editValue() {
		Property property = this;
		Window window;
		if (windowFactory == null) {
			window = new MasterDetailEditWindow("Edit", property, fieldFactory, containedType, instanceTypeProvider, true);
			if (positionX  != -1) {
				window.setPositionX(positionX);
			}
		} else {
			window = windowFactory.createWindow(property, containedType, fieldFactory);
		}
		WindowUtil.getRoot(getWindow()).addWindow(window);
	}
}
