package masquerade.sim.app.ui2.dialog.view.impl;

import masquerade.sim.app.ui2.dialog.view.EditNamespacePrefixView;
import masquerade.sim.app.util.NamespacePrefix;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class EditNamespacePrefixViewImpl extends Window implements EditNamespacePrefixView {

	private final Window parent;
	private final Form form;
	private EditNamespacePrefixViewCallback callback;
	
	private String originalPrefix;

	public EditNamespacePrefixViewImpl(Window parent) {
		this.parent = parent;
		
		setCaption("Namespace Prefix");
		setModal(true);
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setWidth("400px");
		
		form = new Form();
		form.setInvalidCommitted(false);
		form.setValidationVisible(true);
		form.setImmediate(true);
		form.setValidationVisibleOnCommit(true);
		form.setWriteThrough(false);
		form.setFormFieldFactory(new FormFieldFactory() {
			@Override
			public Field createField(Item item, Object propertyId, Component uiContext) {
				if (NamespacePrefix.PROP_PREFIX.equals(propertyId)) {
					TextField field = new TextField("Prefix");
					field.setImmediate(true);
					field.setRequired(true);
					field.addValidator(createUniquePrefixValidator());
					return field;
				} else if (NamespacePrefix.PROP_URI.equals(propertyId)) {
					TextField field = new TextField("URI");
					field.setImmediate(true);
					field.setWidth("300px");
					field.setRequired(true);
					return field;
				} else {
					throw new IllegalArgumentException("Unkown property: " + propertyId);
				}
			}
		});
		layout.addComponent(form);
		
		Button okButton = new Button("OK", new ClickListener() {
			@SuppressWarnings("unchecked") 
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					form.commit();
				
					BeanItem<NamespacePrefix> item = (BeanItem<NamespacePrefix>) form.getItemDataSource();
					NamespacePrefix pfx = item.getBean();
					
					callback.onPrefixUpdated(originalPrefix, pfx.getPrefix(), pfx.getUri());
					close();
				} catch (InvalidValueException ex) {
					// Avoid closing the window, let the form
					// handle validation errors
				}
			}
		});
		layout.addComponent(okButton);
		layout.setComponentAlignment(okButton, Alignment.MIDDLE_RIGHT);
		layout.setExpandRatio(form, 1.0f);
		setContent(layout);
	}

	@Override
	public void show(EditNamespacePrefixViewCallback callback, String prefix, String uri) {
		this.callback = callback;
		originalPrefix = prefix;
		
		NamespacePrefix pfx = new NamespacePrefix(prefix, uri);
		BeanItem<?> item = new BeanItem<NamespacePrefix>(pfx);
		form.setItemDataSource(item);
		
		center();
		WindowUtil.getRoot(parent).addWindow(this);		
	}

	private Validator createUniquePrefixValidator() {
		return new AbstractValidator("This prefix is already defined, please choose another prefix") {
			@Override
			public boolean isValid(Object value) {
				String pfx = (String) value;
				
				if (originalPrefix.equals(pfx)) {
					return true;
				}
					
				return callback.isPrefixAvailable(pfx);
			}
		};
	}
}
