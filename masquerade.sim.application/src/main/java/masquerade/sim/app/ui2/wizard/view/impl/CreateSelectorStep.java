package masquerade.sim.app.ui2.wizard.view.impl;

import java.util.Collection;
import java.util.UUID;

import masquerade.sim.model.RequestMapping;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Wizard step for creating a simulation's request selector
 */
public class CreateSelectorStep implements WizardStep {

	private final Collection<Class<?>> selectorTypes;
	private final FormFieldFactory fieldFactory;
	private VerticalLayout layout;
	private Select selectorTypeDropdown;
	private Form form;

	public CreateSelectorStep(Collection<Class<?>> selectorTypes, FormFieldFactory fieldFactory) {
		if (selectorTypes.isEmpty())
			throw new IllegalArgumentException("Selector wizard step requires at least one selector type to be available");
		this.selectorTypes = selectorTypes;
		this.fieldFactory = fieldFactory;
	}

	@Override
	public String getCaption() {
		return "Selector";
	}

	@SuppressWarnings("serial")
	@Override
	public Component getContent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.addComponent(new Label("Please choose the type of selector activating this simulation when a matching request is received on a channel:"));

			Class<?> initialSelectorType = selectorTypes.iterator().next();
			selectorTypeDropdown = WizardUiUtils.createTypeSelectDropdown(selectorTypes);
			layout.addComponent(selectorTypeDropdown);

			// DetailLayout
			GridLayout detailLayout = new GridLayout();
			detailLayout.setSizeFull();
			detailLayout.setMargin(false, false, false, true);
			layout.addComponent(detailLayout);

			// Wrap form into a panel to get scroll bars if it cannot be
			// displayed fully
			final Panel panel = new Panel();
			panel.setSizeFull();
			panel.addStyleName(Reindeer.PANEL_LIGHT);
			panel.addComponent(createInitialForm(initialSelectorType));
			detailLayout.addComponent(panel);

			// Reset form on type selection
			selectorTypeDropdown.addListener(new ValueChangeListener() {
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

	private Component createInitialForm(Class<?> initialSelectorType) {
		// TODO: Refactor error handling/notification out of ModelObjectFactory
		// TODO: Remove names on non-toplevel simulation objects, keep only IDs
		// on Simulation and Channel
		Object bean = ModelObjectFactory.createModelObject(layout.getWindow(), initialSelectorType, UUID.randomUUID().toString());
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

	
	public RequestMapping<?> getSelector() {
		BeanItem<?> item = (BeanItem<?>) form.getItemDataSource();
		return (RequestMapping<?>) item.getBean();
	}
}
