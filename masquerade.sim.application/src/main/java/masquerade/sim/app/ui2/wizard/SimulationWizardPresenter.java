package masquerade.sim.app.ui2.wizard;

import java.util.Collection;

import masquerade.sim.app.ui2.wizard.SimulationWizard.SimulationWizardCallback;
import masquerade.sim.app.ui2.wizard.view.SimulationWizardView;
import masquerade.sim.app.ui2.wizard.view.SimulationWizardView.SimulationViewCallback;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.plugin.PluginRegistry;

/**
 * Presenter for {@link SimulationWizardView}
 */
public class SimulationWizardPresenter implements SimulationViewCallback {

	private final SimulationWizardView view;
	private final PluginRegistry pluginRegistry;

	public SimulationWizardPresenter(SimulationWizardView view, PluginRegistry pluginRegistry) {
		this.view = view;
		this.pluginRegistry = pluginRegistry;
	}

	public void showWizard(SimulationWizardCallback callback) {
		view.showWizard();
	}

	@Override
	public Collection<Class<?>> getSelectorTypes() {
		return pluginRegistry.getExtensions(RequestMapping.class);
	}

	@Override
	public Collection<Class<?>> getRequestIdProviderTypes() {
		return pluginRegistry.getExtensions(RequestIdProvider.class);
	}
}
