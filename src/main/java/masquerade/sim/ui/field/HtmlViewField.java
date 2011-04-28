package masquerade.sim.ui.field;

import org.vaadin.addon.customfield.CustomField;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * A read-only {@link Field} showing some HTML
 */
public class HtmlViewField extends CustomField {

	private Label htmlLabel;

	/**
	 * @param type Type of the value being edited
	 */
	public HtmlViewField() {
		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setWidth("100%");
		
		htmlLabel = new Label();
		htmlLabel.setContentMode(Label.CONTENT_XHTML);
		htmlLabel.setWidth("100%");
		mainLayout.addComponent(htmlLabel);
		mainLayout.setComponentAlignment(htmlLabel, Alignment.MIDDLE_LEFT);
		
		setCompositionRoot(mainLayout);
	}
	
	/**
	 * Set value as HTML to label
	 */
	@Override
	protected void setInternalValue(Object newValue) {
		super.setInternalValue(newValue);
		htmlLabel.setValue(newValue);
	}

	@Override
	public Class<?> getType() {
		return String.class;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}
}
