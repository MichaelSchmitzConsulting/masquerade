package masquerade.sim.app.ui.wizard.view;

import java.util.Collection;

import masquerade.sim.app.ui.wizard.SimulationWizard.SimulationWizardCallback;
import masquerade.sim.model.listener.CreateApprover;

import com.vaadin.ui.FormFieldFactory;

/**
 * Interface for the dialog supporting Simulation creation.
 */
public interface SimulationWizardView {

	interface SimulationWizardViewCallback extends SimulationWizardCallback, CreateApprover {
		Collection<Class<?>> getScriptTypes();
		Collection<Class<?>> getSelectorTypes();
		Collection<Class<?>> getRequestIdProviderTypes();
		FormFieldFactory getFormFieldFactory();
	}
	
	void showWizard();
}
