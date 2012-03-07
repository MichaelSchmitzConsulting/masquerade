package masquerade.sim.app.ui2.factory.impl;

import masquerade.sim.app.ui2.factory.SimulationFactory;
import masquerade.sim.app.ui2.wizard.SimulationWizard;
import masquerade.sim.app.ui2.wizard.SimulationWizard.SimulationWizardCallback;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.impl.DefaultSimulation;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.ui.Window;

public class SimulationFactoryImpl implements SimulationFactory {

	private final Window window;
	private final PluginRegistry pluginRegistry;
	private final ModelRepository modelRepository;

	public SimulationFactoryImpl(Window window, PluginRegistry pluginRegistry, ModelRepository modelRepository) {
		this.window = window;
		this.pluginRegistry = pluginRegistry;
		this.modelRepository = modelRepository;
	}

	@Override
	public void createSimulation(final SimulationFactoryCallback callback) {
		SimulationWizardCallback wizardCallback = new SimulationWizardCallback() {
			@Override public void onWizardComplete(String simulationId, RequestMapping<?> selector, RequestIdProvider<?> idProvider) {
				Script script = new SequenceScript(simulationId + "Script");
				Simulation simulation = new DefaultSimulation(simulationId, selector, idProvider, script);
				callback.onCreate(simulation);
			}
		};
		SimulationWizard.showWizard(wizardCallback, window, pluginRegistry);
	}
}
