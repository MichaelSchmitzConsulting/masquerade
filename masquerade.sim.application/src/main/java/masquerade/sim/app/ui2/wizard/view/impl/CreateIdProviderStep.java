package masquerade.sim.app.ui2.wizard.view.impl;

import java.util.Collection;

import masquerade.sim.model.RequestIdProvider;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * Wizard step for creating a {@link RequestIdProvider}
 */
public class CreateIdProviderStep implements WizardStep {

	private final Collection<Class<?>> idProviderTypes;
	private ComponentContainer layout;
	private Select selectorTypeDropdown;

	public CreateIdProviderStep(Collection<Class<?>> idProviderTypes) {
		if (idProviderTypes.isEmpty())
			throw new IllegalArgumentException("Request ID Provider wizard step requires at least one Request ID Provider type to be available");
		this.idProviderTypes = idProviderTypes;
	}

	@Override
	public String getCaption() {
		return "Request ID Provider";
	}

	@Override
	public Component getContent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.addComponent(new Label("Please choose the type of request ID proviuder assigning a unique ID to a request when a simulation is applied:"));
			
	        selectorTypeDropdown = WizardUiUtils.createTypeSelectDropdown(idProviderTypes);
			layout.addComponent(selectorTypeDropdown);
		}
		return layout;
	}

	@Override
	public boolean onAdvance() {
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

	public RequestIdProvider<?> getRequestIdProvider() {
		return null;
	}
}
