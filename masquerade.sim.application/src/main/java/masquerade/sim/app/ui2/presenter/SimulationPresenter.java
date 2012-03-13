package masquerade.sim.app.ui2.presenter;

import java.util.ArrayList;
import java.util.List;

import masquerade.sim.app.ui2.factory.SimulationFactory;
import masquerade.sim.app.ui2.factory.SimulationFactory.SimulationFactoryCallback;
import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.app.ui2.view.impl.SimulationViewImpl.SimulationInfo;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationWrapper;

public class SimulationPresenter implements SimulationView.SimulationViewCallback {

	private final SimulationView view;
	private final ModelRepository modelRepository;
	private final SimulationFactory simulationFactory;
	private Simulation currentSelection = null;
	
	public SimulationPresenter(SimulationView view, ModelRepository modelRepository, SimulationFactory simulationFactory) {
		this.view = view;
		this.modelRepository = modelRepository;
		this.simulationFactory = simulationFactory;
	}

	@Override
	public void onSimulationSelected(String id) {
		if (id == null) {
			currentSelection = null;
			view.deselectSimulation();
			return;
		}
		
		Simulation simulation = modelRepository.getSimulationForUpdate(id);
		currentSelection = simulation;
		if (simulation == null) {
			view.deselectSimulation();
			// Selected simulation does no longer exist in repository, refresh view 
			onRefresh();
		} else {
			view.setCurrentSimulation(simulation, modelRepository.getAllChannelIds(), modelRepository.getChannelsForSimulation(simulation.getId()));
		}
	}

	@Override
	public void onRemove(String id) {
		modelRepository.deleteSimulation(id);
		view.deselectSimulation();
		view.setSelection(null);
		currentSelection = null;
		onRefresh();
	}

	@Override
	public void onAdd() {
		simulationFactory.createSimulation(new SimulationFactoryCallback() {
			@Override
			public void onCreate(Simulation simulation) {
				modelRepository.insertSimulation(simulation, true);
				onRefresh();
				String simulationId = simulation.getId();
				view.setSelection(simulationId);
				view.setCurrentSimulation(simulation, modelRepository.getAllChannelIds(), modelRepository.getChannelsForSimulation(simulationId));
				currentSelection = simulation;
			}
		});
	}

	@Override
	public void onRefresh() {
		List<SimulationInfo> simulations = new ArrayList<SimulationInfo>();
		for (SimulationWrapper wrapper : modelRepository.listSimulations()) {
			Simulation simulation = wrapper.getSimulation();
			SimulationInfo info = new SimulationInfo(simulation.getId(), wrapper.isPersistent());
			simulations.add(info);
		}
		view.setSelection(null);
		currentSelection = null;
		view.setSimulationList(simulations);
	}

	@Override
	public void onSave() {
		if (currentSelection != null) {
			modelRepository.insertSimulation(currentSelection, true, view.getChannelAssignments());
			
			view.deselectSimulation();
			view.setSelection(null);
			currentSelection = null;
		}
	}

}
