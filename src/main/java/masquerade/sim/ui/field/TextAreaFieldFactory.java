package masquerade.sim.ui.field;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

/**
 * Creates a {@link TextArea} field
 */
public class TextAreaFieldFactory implements FieldFactory {

	private String caption;
	
	public TextAreaFieldFactory(String caption) {
		this.caption = caption;
	}

	@Override
	public Field createField(Object existingValue) {
		TextArea field = new TextArea();
		field.setWidth("350px");
		field.setHeight("180px");
		field.setValue(existingValue);
		field.setCaption(caption);
		return field;
	}
}
