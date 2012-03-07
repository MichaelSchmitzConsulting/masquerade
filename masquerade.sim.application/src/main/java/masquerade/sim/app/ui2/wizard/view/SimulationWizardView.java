package masquerade.sim.app.ui2.wizard.view;

import java.util.Collection;

/**
 * Interface for the dialog supporting Simulation creation.
 */
public interface SimulationWizardView {

	interface SimulationViewCallback {
		Collection<Class<?>> getSelectorTypes();
		Collection<Class<?>> getRequestIdProviderTypes();
	}
	
	void showWizard();
	void closeWizard();
}
