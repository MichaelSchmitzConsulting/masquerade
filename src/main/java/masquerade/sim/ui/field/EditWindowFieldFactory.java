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
	private int positionX = -1;
	private boolean isAllowItemReordering = false;

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

	/**
	 * @param positionY the positionY to set
	 */
	public void setPositionX(int positionY) {
		this.positionX = positionY;
	}

	/**
	 * @param isAllowItemReordering the isAllowItemReordering to set
	 */
	public void setAllowItemReordering(boolean isAllowItemReordering) {
		this.isAllowItemReordering = isAllowItemReordering;
	}

	@Override
	public Field createField(Object existingValue) {
		EditWindowField field = new EditWindowField(Collection.class, containedType, instanceTypes, fieldFactory, windowFactory);
		field.setAllowItemReordering(isAllowItemReordering);
		if (positionX  != -1) {
			field.setPositionX(positionX);
		}
		field.setCaption(caption);
		return field;
	}

}
