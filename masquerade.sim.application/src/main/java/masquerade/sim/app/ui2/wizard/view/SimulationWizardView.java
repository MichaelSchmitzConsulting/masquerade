package masquerade.sim.app.ui2.wizard.view;

import java.util.Collection;

import masquerade.sim.app.ui2.wizard.SimulationWizard.SimulationWizardCallback;
import masquerade.sim.model.listener.CreateApprover;

import com.vaadin.ui.FormFieldFactory;

/**
 * Interface for the dialog supporting Simulation creation.
 */
public interface SimulationWizardView {

	interface SimulationWizardViewCallback extends SimulationWizardCallback, CreateApprover {
		Collection<Class<?>> getSelectorTypes();
		Collection<Class<?>> getRequestIdProviderTypes();
		FormFieldFactory getFormFieldFactory();
	}
	
	void showWizard();
}
