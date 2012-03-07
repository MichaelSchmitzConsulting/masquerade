package masquerade.sim.app.ui2.wizard.view.impl;

import java.util.Collection;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * Wizard step for creating a simulation's request selector
 */
public class CreateSelectorStep implements WizardStep {

	private final Collection<Class<?>> selectorTypes;
	private ComponentContainer layout;
	private Select selectorTypeDropdown;

	public CreateSelectorStep(Collection<Class<?>> selectorTypes) {
		this.selectorTypes = selectorTypes;
	}

	@Override
	public String getCaption() {
		return "Selector";
	}

	@Override
	public Component getContent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.addComponent(new Label("Please choose the type of selector activating this simulation when a matching request is received on a channel:"));
			
	        selectorTypeDropdown = WizardUiUtils.createTypeSelectDropdown(selectorTypes);
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
}
