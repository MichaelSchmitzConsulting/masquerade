package masquerade.sim.app.ui2.wizard;

import masquerade.sim.app.ui2.wizard.view.impl.SimulationWizardViewImpl;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Simulation;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.ui.Window;

/**
 * Guides the user through creating a new {@link Simulation}. Supports
 * creating a {@link RequestMapping selector} and a {@link RequestIdProvider}.
 */
public class SimulationWizard {
	public interface SimulationWizardCallback {
		void onWizardComplete(String simulationId, RequestMapping<?> selector, RequestIdProvider<?> idProvider);
	}
	
	public static void showWizard(SimulationWizardCallback callback, Window parent, PluginRegistry pluginRegistry) {
		SimulationWizardViewImpl view = new SimulationWizardViewImpl(parent);
		SimulationWizardPresenter presenter = new SimulationWizardPresenter(view, pluginRegistry);
		view.bind(presenter);
		presenter.showWizard(callback);
	}
	
	private SimulationWizard() { }
}
