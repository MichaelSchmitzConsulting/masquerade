package masquerade.sim.app.ui2.wizard.view.impl;

import masquerade.sim.model.listener.CreateApprover;

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

	private final CreateApprover approver;
	private ComponentContainer layout;
	private TextField simulationIdText;

	public CreateSimulationStep(CreateApprover approver) {
		this.approver = approver;
	}

	@Override
	public String getCaption() {
		return "Simulation";
	}

	@Override
	public Component getContent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.addComponent(new Label("Please enter a name for the Simulation"));
			
			simulationIdText = new TextField(null, "newSimulation");
			simulationIdText.setImmediate(true);
			layout.addComponent(simulationIdText);		
		}
		return layout;
	}

	@Override
	public boolean onAdvance() {
		if (StringUtils.isBlank(getSimulationId())) {
			simulationIdText.setComponentError(new UserError("Please enter a simulation ID"));
			return false;
		}
		if (approver.isNameUsed(getSimulationId())) {
			simulationIdText.setComponentError(new UserError("This simulation ID is already being used, please enter a different ID"));
			return false;
		}
		return true;
	}

	@Override
	public boolean onBack() {
		return true;
	}

	public String getSimulationId() {
		return (String) simulationIdText.getValue();
	}
}
