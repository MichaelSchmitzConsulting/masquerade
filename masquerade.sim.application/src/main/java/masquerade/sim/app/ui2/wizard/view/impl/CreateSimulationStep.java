package masquerade.sim.app.ui2.wizard.view.impl;

import org.apache.commons.lang.StringUtils;
import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.terminal.UserError;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Wizard for entering a simulation ID
 */
public class CreateSimulationStep implements WizardStep {

	private ComponentContainer layout;
	private TextField simulationIdText;

	@Override
	public String getCaption() {
		return "Simulation";
	}

	@Override
	public Component getContent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.addComponent(new Label("Simulation ID"));
			
			simulationIdText = new TextField(null, "newSimuation");
			layout.addComponent(simulationIdText);		
		}
		return layout;
	}

	@Override
	public boolean onAdvance() {
		String simId = (String) simulationIdText.getValue();
		boolean canAdvance = StringUtils.isBlank(simId);
		if (!canAdvance) {
			simulationIdText.setComponentError(new UserError("Please enter a simulation ID"));
		}
		return false;
	}

	@Override
	public boolean onBack() {
		return true;
	}
}
