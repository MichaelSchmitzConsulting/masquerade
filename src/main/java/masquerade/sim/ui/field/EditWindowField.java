package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.util.WindowUtil;

import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class EditWindowField extends CustomField {

	public interface WindowFactory {
		Window createWindow(Property property, Class<?> type, FormFieldFactory fieldFactory);
	}
	
	private Class<?> type;
	private FormFieldFactory fieldFactory;
	private Class<?> containedType;
	private Collection<Class<?>> instanceTypes;
	private WindowFactory windowFactory;
	private Label editedLabel;
	private HorizontalLayout mainLayout;
	private int positionX = -1;

	public EditWindowField(Class<?> valueType, Class<?> containedType, Collection<Class<?>> instanceTypes, FormFieldFactory fieldFactory) {
		this(valueType, containedType, instanceTypes, fieldFactory, null);
	}
	
	/**
	 * Creates a field with an 'Edit...' button that opens a modal MasterDetailEditWindow to edit a collection
	 * @param valueType
	 * @param containedType
	 * @param instanceTypes
	 * @param fieldFactory
	 * @param windowFactory
	 */
	public EditWindowField(Class<?> valueType, Class<?> containedType, Collection<Class<?>> instanceTypes, FormFieldFactory fieldFactory, WindowFactory windowFactory) {
		this.type = valueType;
		this.instanceTypes = instanceTypes;
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
		
		createEditedLabel();
		
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

	@Override
	public void commit() throws SourceException, InvalidValueException {
		super.commit();
				
		// Need to recreate label, setting value only does not correctly update it in the UI 
		mainLayout.removeComponent(editedLabel);
		createEditedLabel();
		editedLabel.setValue("<font color=\"green\">Saved</font>");
	}

	private void createEditedLabel() {
		editedLabel = new Label();
		editedLabel.setContentMode(Label.CONTENT_XHTML);
		editedLabel.setWidth("350px");
		mainLayout.addComponent(editedLabel);
		mainLayout.setComponentAlignment(editedLabel, Alignment.MIDDLE_LEFT);
	}
	
	private void editValue() {
		Property property = this;
		Window window;
		if (windowFactory == null) {
			window = new MasterDetailEditWindow("Edit", property, fieldFactory, containedType, instanceTypes);
			if (positionX  != -1) {
				window.setPositionX(positionX);
			}
		} else {
			window = windowFactory.createWindow(property, containedType, fieldFactory);
		}
		WindowUtil.getRoot(getWindow()).addWindow(window);
		
		final Object oldValue = property.getValue();
		addListener(new ValueChangeListener() {
			@Override public void valueChange(Property.ValueChangeEvent event) {
				Object value = event.getProperty().getValue();
				if (!oldValue.equals(value)) {
					editedLabel.setValue("<font color=\"red\">Value changed, please save</font>");
				}
			}
		});
	}
}
