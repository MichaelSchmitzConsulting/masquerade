package masquerade.sim.app.ui2.wizard.view;

import java.util.Collection;

import com.vaadin.ui.FormFieldFactory;

/**
 * Interface for the dialog supporting Simulation creation.
 */
public interface SimulationWizardView {

	interface SimulationViewCallback {
		Collection<Class<?>> getSelectorTypes();
		Collection<Class<?>> getRequestIdProviderTypes();
		FormFieldFactory getFormFieldFactory();
	}
	
	void showWizard();
	void closeWizard();
}
