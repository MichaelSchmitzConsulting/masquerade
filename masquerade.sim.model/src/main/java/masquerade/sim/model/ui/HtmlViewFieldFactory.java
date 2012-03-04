package masquerade.sim.model.ui;

import masquerade.sim.plugin.FieldFactory;

import com.vaadin.ui.Field;

/**
 * {@link FieldFactory} for {@link HtmlViewField}
 */
public class HtmlViewFieldFactory implements FieldFactory {

	private String caption;

	/**
	 * @param caption The created field's caption
	 */
	public HtmlViewFieldFactory(String caption) {
		this.caption = caption;
	}

	@Override
	public Field createField(Object existingValue) {
		Field field = new HtmlViewField();
		field.setCaption(caption);
		return field;
	}
}
