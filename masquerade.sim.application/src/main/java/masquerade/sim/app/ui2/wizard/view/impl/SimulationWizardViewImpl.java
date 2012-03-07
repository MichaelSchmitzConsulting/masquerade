package masquerade.sim.app.ui2.wizard.view.impl;

import masquerade.sim.app.ui2.wizard.view.SimulationWizardView;
import masquerade.sim.util.WindowUtil;

import org.vaadin.teemu.wizards.Wizard;

import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SimulationWizardViewImpl extends Window implements SimulationWizardView {
	
	private final Window parent;
	private SimulationViewCallback callback;
	
	public SimulationWizardViewImpl(Window parent) {
		this.parent = parent;
	}
	
	public void bind(SimulationViewCallback callback) {
		this.callback = callback;
	}

	@Override
	public void showWizard() {
		Wizard wizard = new Wizard();
		wizard.addStep(new CreateSimulationStep());
		wizard.addStep(new CreateSelectorStep(callback.getSelectorTypes(), callback.getFormFieldFactory()));
		wizard.addStep(new CreateIdProviderStep(callback.getRequestIdProviderTypes()));

		setContent(wizard);

		WindowUtil.getRoot(parent).addWindow(this);
	}

	@Override
	public void closeWizard() {
		WindowUtil.getRoot(parent).removeWindow(this);
	}
}
