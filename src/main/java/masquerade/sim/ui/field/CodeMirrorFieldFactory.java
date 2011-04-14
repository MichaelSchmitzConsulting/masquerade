package masquerade.sim.ui.field;

import org.vaadin.codemirror.CodeMirror;
import org.vaadin.codemirror.client.ui.CodeStyle;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

/**
 * Creates a {@link TextArea} field
 */
public class CodeMirrorFieldFactory implements FieldFactory {

	private String caption;
	private CodeStyle style;
	
	public CodeMirrorFieldFactory(String caption, CodeStyle style) {
		this.caption = caption;
		this.style = style;
	}

	@Override
	public Field createField(Object existingValue) {
		CodeMirror field = new CodeMirror(caption, style);
		field.setWidth("350px");
		field.setHeight("180px");
		field.setValue(existingValue);
		return field;
	}
}
