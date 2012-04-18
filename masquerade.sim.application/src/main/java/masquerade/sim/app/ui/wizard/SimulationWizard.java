package masquerade.sim.app.ui2.wizard;

import masquerade.sim.app.ui2.wizard.view.impl.SimulationWizardViewImpl;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Window;

/**
 * Guides the user through creating a new {@link Simulation}. Supports
 * creating a {@link RequestMapping selector} and a {@link RequestIdProvider}.
 */
public class SimulationWizard {
	public interface SimulationWizardCallback {
		void onWizardComplete(String simulationId, Script script, RequestMapping<?> selector, RequestIdProvider<?> idProvider);
	}
	
	public static void showWizard(SimulationWizardCallback callback, Window parent, PluginRegistry pluginRegistry, FormFieldFactory fieldFactory, ModelRepository modelRepository) {
		SimulationWizardViewImpl view = new SimulationWizardViewImpl(parent);
		SimulationWizardPresenter presenter = new SimulationWizardPresenter(view, pluginRegistry, fieldFactory, callback, modelRepository);
		view.bind(presenter);
		presenter.showWizard();
	}
	
	private SimulationWizard() { }
}
