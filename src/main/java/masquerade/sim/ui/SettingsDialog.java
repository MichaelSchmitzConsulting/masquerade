package masquerade.sim.ui;

import masquerade.sim.UpdateListener;
import masquerade.sim.model.Settings;
import masquerade.sim.util.StringUtil;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

/**
 * Dialog allowing to change application
 * settings.
 */
public class SettingsDialog extends Window {

	/**
	 * Show settings dialog
	 * @param parent Window to attach the dialog to
	 */
	public static void showModal(Window parent, Settings settings, UpdateListener updateListener) {
		SettingsDialog window = new SettingsDialog(settings, updateListener);
		WindowUtil.getRoot(parent).addWindow(window);
	}

	private UpdateListener updateListener;

	/**
	 * Private constructor, opening the settings dialog
	 * is supported using {@link #showModal(Window)} only.
	 * @param settings 
	 * @param updateListener 
	 */
	private SettingsDialog(Settings settings, UpdateListener updateListener) {
		super("Settings");
		this.updateListener = updateListener;

		setModal(true);
		setWidth("500px");
		buildLayout(settings);
	}

	private void buildLayout(Settings settings) {
		Form form = createForm(settings);
		getContent().addComponent(form);
	}

	private Form createForm(Settings settings) {
		final Form form = new Form();
		form.setFormFieldFactory(new SettingsFieldFactory());
		form.setWidth("500px");
        form.setInvalidCommitted(false);
        
		BeanItem<?> item = new BeanItem<Settings>(settings);
		form.setItemDataSource(item);
		
        // Add save button in form footer
        HorizontalLayout buttons = new HorizontalLayout();
		Button save = new Button("Save", new Button.ClickListener() {
            @Override
			public void buttonClick(ClickEvent event) {
                try {
                    form.commit();
                    
                    BeanItem<?> item = (BeanItem<?>) form.getItemDataSource();
                    updateListener.notifyUpdated(item.getBean());
                    SettingsDialog.this.close();
                } catch (InvalidValueException e) {
                    // Ignored, we'll let the Form handle the errors
                }
            }
        });
		buttons.addComponent(save);
		buttons.setComponentAlignment(save, Alignment.MIDDLE_LEFT);
		form.getFooter().addComponent(buttons);
		form.getFooter().setMargin(true, true, false, false);
		
		return form;
	}
	
	private static final class SettingsFieldFactory extends DefaultFieldFactory {
		@Override
		public Field createField(Item item, Object propertyId, Component uiContext) {
			if ("configurationProperties".equals(propertyId)) {
				String caption = StringUtil.fromCamelCase((String) propertyId);
				Field textArea = new TextArea(caption);
				return textArea;
			} else {
				return super.createField(item, propertyId, uiContext);
			}
		}
	}
}
