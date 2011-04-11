package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.ui.field.EditWindowField.WindowFactory;

import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;

public class EditWindowFieldFactory implements FieldFactory {

	private String caption;
	private FormFieldFactory fieldFactory;
	private Class<?> containedType;
	private Collection<Class<?>> instanceTypes;
	private WindowFactory windowFactory;

	public EditWindowFieldFactory(String caption, FormFieldFactory fieldFactory, Class<?> containedType, Collection<Class<?>> instanceTypes) {
		this(caption, fieldFactory, containedType, instanceTypes, null);
	}
	
	public EditWindowFieldFactory(String caption, FormFieldFactory fieldFactory, Class<?> containedType, Collection<Class<?>> instanceTypes, WindowFactory windowFactory) {
		this.caption = caption;
		this.fieldFactory = fieldFactory;
		this.containedType = containedType;
		this.instanceTypes = instanceTypes;
		this.windowFactory = windowFactory;
	}

	@Override
	public Field createField(Object existingValue) {
		EditWindowField field = new EditWindowField(Collection.class, containedType, instanceTypes, fieldFactory, windowFactory);
		field.setCaption(caption);
		return field;
	}

}
