package masquerade.sim.app.ui.wizard.view.impl;

import masquerade.sim.app.ui.wizard.view.SimulationWizardView;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.util.WindowUtil;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class SimulationWizardViewImpl extends Window implements SimulationWizardView {
	
	private final Window parent;
	private SimulationWizardViewCallback callback;
	
	public SimulationWizardViewImpl(Window parent) {
		this.parent = parent;
	}
	
	public void bind(SimulationWizardViewCallback callback) {
		this.callback = callback;
	}

	@Override
	public void showWizard() {
		final CreateSimulationStep simulationStep = new CreateSimulationStep(callback);
		final CreateBeanStep<Script> scriptStep = 
			new CreateBeanStep<Script>(callback.getScriptTypes(), callback.getFormFieldFactory(), "Script");
		final CreateBeanStep<RequestMapping<?>> selectorStep = 
			new CreateBeanStep<RequestMapping<?>>(callback.getSelectorTypes(), callback.getFormFieldFactory(), "Selector");
		final CreateBeanStep<RequestIdProvider<?>> idProviderStep = 
			new CreateBeanStep<RequestIdProvider<?>>(callback.getRequestIdProviderTypes(), callback.getFormFieldFactory(), "Request ID Provider");

		Wizard wizard = new Wizard();
		wizard.addStep(simulationStep);
		wizard.addStep(scriptStep);
		wizard.addStep(selectorStep);
		wizard.addStep(idProviderStep);

		setCaption("Create Simulation");
		setWidth("600px");
		setHeight("300px");
		setContent(wizard);

		center();
		WindowUtil.getRoot(parent).addWindow(this);

		wizard.addListener(new WizardProgressListener() {
			@Override public void wizardCompleted(WizardCompletedEvent event) {
				String simulationId = simulationStep.getSimulationId();
				RequestMapping<?> selector = selectorStep.getBean();
				RequestIdProvider<?> idProvider = idProviderStep.getBean();
				Script script = scriptStep.getBean();
				callback.onWizardComplete(simulationId, script, selector, idProvider);
				closeWizard();
			}
			@Override public void wizardCancelled(WizardCancelledEvent event) {
				closeWizard();
			}
			@Override public void stepSetChanged(WizardStepSetChangedEvent event) { }
			@Override public void activeStepChanged(WizardStepActivationEvent event) { }
		});
	}

	private void closeWizard() {
		WindowUtil.getRoot(parent).removeWindow(this);
	}
}
