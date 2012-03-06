package masquerade.sim.app.ui2.factory.impl;

import com.vaadin.ui.Window;

import masquerade.sim.app.ui2.factory.SimulationFactory;
import masquerade.sim.model.ModelInstanceTypeProvider;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.ui.CreateObjectDialog;
import masquerade.sim.plugin.PluginRegistry;

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
		ModelInstanceTypeProvider instanceTypeProvider = new ModelInstanceTypeProvider(Simulation.class, pluginRegistry);
		CreateApprover createApprover = new CreateApprover() {
			@Override
			public boolean isNameUsed(Class<?> baseType, String usedName) {
				return modelRepository.getSimulation(usedName) != null;
			}
			@Override
			public boolean canCreate(Class<?> type, String name, StringBuilder errorMsg) {
				if (isNameUsed(Simulation.class, name)) {
					errorMsg.append("Simulation with ID " + name + " already exists");
					return false;
				} else {
					return true;
				}
			}
		};
		CreateListener createListener = new CreateListener() {
			@Override
			public void notifyCreate(Object value) {
				Simulation simulation = (Simulation) value;
				modelRepository.insertSimulation(simulation);
				callback.onCreate(simulation);
			}
		};
		
		CreateObjectDialog.showModal(window, "Create Simulation", "newSimulationId", createListener, createApprover, instanceTypeProvider);
	}
}
