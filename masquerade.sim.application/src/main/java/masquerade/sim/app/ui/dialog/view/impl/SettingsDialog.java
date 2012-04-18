package masquerade.sim.app.ui2.dialog.view.impl;

import masquerade.sim.model.Settings;
import masquerade.sim.model.listener.UpdateListener;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

/**
 * Dialog allowing to change application
 * settings.
 */
@SuppressWarnings("serial")
public class SettingsDialog extends Window {

	/**
	 * Show settings dialog
	 * @param parent Window to attach the dialog to
	 * @param versionInformation 
	 */
	public static void showModal(Window parent, Settings settings, UpdateListener updateListener, String versionInformation) {
		SettingsDialog window = new SettingsDialog(settings, updateListener, versionInformation);
		WindowUtil.getRoot(parent).addWindow(window);
	}

	private UpdateListener updateListener;

	/**
	 * Private constructor, opening the settings dialog
	 * is supported using {@link #showModal(Window)} only.
	 * @param settings 
	 * @param updateListener 
	 * @param versionInformation 
	 */
	private SettingsDialog(Settings settings, UpdateListener updateListener, String versionInformation) {
		super("Settings");
		this.updateListener = updateListener;

		setModal(true);
		setWidth("600px");
		buildLayout(settings, versionInformation);
	}

	private void buildLayout(Settings settings, String versionInformation) {
		Label header = new Label(versionInformation);
		getContent().addComponent(header);
		
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
			}
			
			return super.createField(item, propertyId, uiContext);
		}
	}
}