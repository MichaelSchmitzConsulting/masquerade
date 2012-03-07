package masquerade.sim.app.ui2.wizard;

import java.util.Collection;

import masquerade.sim.app.ui2.wizard.SimulationWizard.SimulationWizardCallback;
import masquerade.sim.app.ui2.wizard.view.SimulationWizardView;
import masquerade.sim.app.ui2.wizard.view.SimulationWizardView.SimulationWizardViewCallback;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.ui.FormFieldFactory;

/**
 * Presenter for {@link SimulationWizardView}
 */
public class SimulationWizardPresenter implements SimulationWizardViewCallback {

	private final SimulationWizardView view;
	private final PluginRegistry pluginRegistry;
	private final FormFieldFactory fieldFactory;
	private final SimulationWizardCallback delegate;

	public SimulationWizardPresenter(SimulationWizardView view, PluginRegistry pluginRegistry, FormFieldFactory fieldFactory, SimulationWizardCallback delegate) {
		this.view = view;
		this.pluginRegistry = pluginRegistry;
		this.fieldFactory = fieldFactory;
		this.delegate = delegate;
	}

	public void showWizard() {
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

	@Override
	public FormFieldFactory getFormFieldFactory() {
		return fieldFactory;
	}

	@Override
	public void onWizardComplete(String simulationId, RequestMapping<?> selector, RequestIdProvider<?> idProvider) {
		delegate.onWizardComplete(simulationId, selector, idProvider);
	}
}
