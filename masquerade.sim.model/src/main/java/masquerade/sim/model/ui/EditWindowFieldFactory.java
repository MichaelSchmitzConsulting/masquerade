package masquerade.sim.model.ui;

import java.util.Collection;

import masquerade.sim.model.ui.EditWindowField.WindowFactory;
import masquerade.sim.plugin.FieldFactory;

import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;

public class EditWindowFieldFactory implements FieldFactory {

	private String caption;
	private FormFieldFactory fieldFactory;
	private Class<?> containedType;
	private WindowFactory windowFactory;
	private int positionX = -1;
	private InstanceTypeProvider instanceTypeProvider;

	public EditWindowFieldFactory(String caption, FormFieldFactory fieldFactory, Class<?> containedType, InstanceTypeProvider instanceTypeProvider) {
		this(caption, fieldFactory, containedType, (WindowFactory) null);
		this.instanceTypeProvider = instanceTypeProvider;
	}
	
	public EditWindowFieldFactory(String caption, FormFieldFactory fieldFactory, Class<?> containedType, WindowFactory windowFactory) {
		this.caption = caption;
		this.fieldFactory = fieldFactory;
		this.containedType = containedType;
		this.windowFactory = windowFactory;
	}

	/**
	 * @param positionY the positionY to set
	 */
	public void setPositionX(int positionY) {
		this.positionX = positionY;
	}

	@Override
	public Field createField(Object existingValue) {
		EditWindowField field = new EditWindowField(Collection.class, containedType, instanceTypeProvider, fieldFactory, windowFactory);
		if (positionX  != -1) {
			field.setPositionX(positionX);
		}
		field.setCaption(caption);
		return field;
	}

}
