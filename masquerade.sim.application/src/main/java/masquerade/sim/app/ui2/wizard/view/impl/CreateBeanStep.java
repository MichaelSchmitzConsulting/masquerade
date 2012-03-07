package masquerade.sim.app.ui2.wizard.view.impl;

import java.util.Collection;
import java.util.UUID;

import masquerade.sim.model.ui.ModelObjectFactory;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Wizard step for creating beans using a generated form
 */
public class CreateBeanStep<B> implements WizardStep {

	private final Collection<Class<?>> beanTypes;
	private final FormFieldFactory fieldFactory;
	private final String caption;
	private VerticalLayout layout;
	private Select typeDropdown;
	private Form form;

	public CreateBeanStep(Collection<Class<?>> beanTypes, FormFieldFactory fieldFactory, String caption) {
		if (beanTypes.isEmpty())
			throw new IllegalArgumentException("Create Bean wizard step requires at least one type of bean to be available");
		this.beanTypes = beanTypes;
		this.fieldFactory = fieldFactory;
		this.caption = caption;
	}

	@Override
	public String getCaption() {
		return caption;
	}

	@SuppressWarnings("serial")
	@Override
	public Component getContent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.setSpacing(true);

			Class<?> initialType = beanTypes.iterator().next();
			typeDropdown = WizardUiUtils.createTypeSelectDropdown(beanTypes);
			layout.addComponent(typeDropdown);

			// DetailLayout
			GridLayout detailLayout = new GridLayout();
			detailLayout.setSizeFull();
			layout.addComponent(detailLayout);

			// Wrap form into a panel to get scroll bars if it cannot be
			// displayed fully
			VerticalLayout panelLayout = new VerticalLayout();
			panelLayout.setMargin(false);
			final Panel panel = new Panel(panelLayout);
			panel.setSizeFull();
			panel.addStyleName(Reindeer.PANEL_LIGHT);
			panel.addComponent(createInitialForm(initialType));
			detailLayout.addComponent(panel);

			// Reset form on type selection
			typeDropdown.addListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					panel.removeAllComponents();
					Class<?> type = (Class<?>) event.getProperty().getValue();
					panel.addComponent(createInitialForm(type));
				}
			});

			layout.addComponent(detailLayout);
			layout.setExpandRatio(detailLayout, 1.0f);

		}
		return layout;
	}

	private Component createInitialForm(Class<?> initialType) {
		// TODO: Refactor error handling/notification out of ModelObjectFactory
		// TODO: Remove names on non-toplevel simulation objects, keep only IDs
		// on Simulation and Channel
		Object bean = ModelObjectFactory.createModelObject(layout.getWindow(), initialType, UUID.randomUUID().toString());
		return createForm(bean);
	}

	private Component createForm(Object bean) {
		form = new Form();
		form.setSizeFull();
		String shortTypeName = bean.getClass().getSimpleName();
		form.setCaption(shortTypeName);
		form.setWriteThrough(true);
		form.setInvalidCommitted(false);
		form.setFormFieldFactory(fieldFactory);

		BeanItem<?> item = new BeanItem<Object>(bean);
		form.setItemDataSource(item);

		form.getLayout().setSizeFull();

		return form;
	}

	@Override
	public boolean onAdvance() {
		try {
			form.commit();
			return true;
		} catch (InvalidValueException ex) {
			return false;
		} catch (SourceException ex) {
			return false;
		}
	}

	@Override
	public boolean onBack() {
		return true;
	}

	
	@SuppressWarnings("unchecked")
	public B getBean() {
		BeanItem<?> item = (BeanItem<?>) form.getItemDataSource();
		return (B) item.getBean();
	}
}
