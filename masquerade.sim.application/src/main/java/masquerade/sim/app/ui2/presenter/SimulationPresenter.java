package masquerade.sim.app.ui2.presenter;

import java.util.ArrayList;
import java.util.List;

import masquerade.sim.app.ui2.factory.SimulationFactory;
import masquerade.sim.app.ui2.factory.SimulationFactory.SimulationFactoryCallback;
import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ModelRepository;

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
			view.setCurrentSimulation(null);
			return;
		}
		
		Simulation simulation = modelRepository.getSimulationForUpdate(id);
		currentSelection = simulation;
		view.setCurrentSimulation(simulation);			
		if (simulation == null) {
			// Selected simulation does no longer exist in repository, refresh view 
			onRefresh();
		}
	}

	@Override
	public void onRemove(String id) {
		modelRepository.deleteSimulation(id);
		view.setCurrentSimulation(null);
		view.setSelection(null);
		currentSelection = null;
		onRefresh();
	}

	@Override
	public void onAdd() {
		simulationFactory.createSimulation(new SimulationFactoryCallback() {
			@Override
			public void onCreate(Simulation simulation) {
				modelRepository.insertSimulation(simulation);
				onRefresh();
				view.setSelection(simulation.getId());
				view.setCurrentSimulation(simulation);
				currentSelection = simulation;
			}
		});
	}

	@Override
	public void onRefresh() {
		List<String> simulations = new ArrayList<String>();
		for (Simulation simulation : modelRepository.getSimulations()) {
			simulations.add(simulation.getId());
		}
		view.setSelection(null);
		currentSelection = null;
		view.setSimulationList(simulations);
	}

	@Override
	public void onSave() {
		if (currentSelection != null) {
			modelRepository.insertSimulation(currentSelection);
			view.setCurrentSimulation(null);
			view.setSelection(null);
			currentSelection = null;
		}
	}

}
