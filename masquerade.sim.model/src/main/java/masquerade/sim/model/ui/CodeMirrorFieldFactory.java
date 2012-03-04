package masquerade.sim.model.ui;

import masquerade.sim.plugin.FieldFactory;

import org.vaadin.codemirror2.CodeMirror;
import org.vaadin.codemirror2.client.ui.CodeMode;

import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

/**
 * Creates a {@link TextArea} field
 */
public class CodeMirrorFieldFactory implements FieldFactory {

	private String caption;
	private CodeMode style;
	
	public CodeMirrorFieldFactory(String caption, CodeMode style) {
		this.caption = caption;
		this.style = style;
	}

	@Override
	public Field createField(Object existingValue) {
		CodeMirror field = new CodeMirror(caption, style);
		field.setWidth("350px");
		field.setHeight("180px");
		return field;
	}
}
