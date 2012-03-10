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
	
	public SimulationPresenter(SimulationView view, ModelRepository modelRepository, SimulationFactory simulationFactory) {
		this.view = view;
		this.modelRepository = modelRepository;
		this.simulationFactory = simulationFactory;
	}

	@Override
	public void onSimulationSelected(String id) {
		if (id == null) {
			return;
		}
		
		Simulation simulation = modelRepository.getSimulationForUpdate(id);
		if (simulation != null) {
			view.setCurrentSimulation(simulation);			
		} else {
			// Selected simulation does no longer exist in repository, refresh view 
			onRefresh();
		}
	}

	@Override
	public void onRemove(String id) {
		modelRepository.deleteChannel(id);
		onRefresh();
	}

	@Override
	public void onAdd() {
		simulationFactory.createSimulation(new SimulationFactoryCallback() {
			@Override
			public void onCreate(Simulation simulation) {
				modelRepository.insertSimulation(simulation);
				onRefresh();
			}
		});
	}

	@Override
	public void onRefresh() {
		List<String> simulations = new ArrayList<String>();
		for (Simulation simulation : modelRepository.getSimulations()) {
			simulations.add(simulation.getId());
		}
		view.setSimulationList(simulations);
	}

}
