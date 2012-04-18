package masquerade.sim.app.ui.wizard;

import java.util.Collection;

import masquerade.sim.app.ui.wizard.SimulationWizard.SimulationWizardCallback;
import masquerade.sim.app.ui.wizard.view.SimulationWizardView;
import masquerade.sim.app.ui.wizard.view.SimulationWizardView.SimulationWizardViewCallback;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.repository.ModelRepository;
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
	private final ModelRepository modelRepository;

	public SimulationWizardPresenter(SimulationWizardView view, PluginRegistry pluginRegistry, FormFieldFactory fieldFactory, SimulationWizardCallback delegate, ModelRepository modelRepository) {
		this.view = view;
		this.pluginRegistry = pluginRegistry;
		this.fieldFactory = fieldFactory;
		this.delegate = delegate;
		this.modelRepository = modelRepository;
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
	public Collection<Class<?>> getScriptTypes() {
		return pluginRegistry.getExtensions(Script.class);
	}

	@Override
	public FormFieldFactory getFormFieldFactory() {
		return fieldFactory;
	}

	@Override
	public void onWizardComplete(String simulationId, Script script, RequestMapping<?> selector, RequestIdProvider<?> idProvider) {
		delegate.onWizardComplete(simulationId, script, selector, idProvider);
	}

	@Override
	public boolean isNameUsed(String id) {
		return modelRepository.containsSimulation(id);
	}
}
