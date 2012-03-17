package masquerade.sim.app.ui2.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masquerade.sim.app.ui.Refreshable;
import masquerade.sim.app.ui2.dialog.view.EditNamespacePrefixView.EditNamespacePrefixViewCallback;
import masquerade.sim.app.ui2.dialog.view.impl.EditNamespacePrefixViewImpl;
import masquerade.sim.app.ui2.factory.SimulationFactory;
import masquerade.sim.app.ui2.factory.SimulationFactory.SimulationFactoryCallback;
import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.app.ui2.view.impl.SimulationViewImpl.SimulationInfo;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationWrapper;

import com.vaadin.ui.Window;

public class SimulationPresenter implements SimulationView.SimulationViewCallback, Refreshable, EditNamespacePrefixViewCallback {

	private static final String DEFAULT_URI = "http://example.com/ns";
	
	private final SimulationView view;
	private final ModelRepository modelRepository;
	private final SimulationFactory simulationFactory;
	private final Window parent;
	private Simulation currentSelection = null;
	private Map<String, String> namespaces = new HashMap<String, String>();
	
	public SimulationPresenter(SimulationView view, ModelRepository modelRepository, SimulationFactory simulationFactory, Window parent) {
		this.view = view;
		this.modelRepository = modelRepository;
		this.simulationFactory = simulationFactory;
		this.parent = parent;
	}

	@Override
	public void onSimulationSelected(String id) {
		if (id == null) {
			setCurrentSelection(null);
			view.deselectSimulation();
			return;
		}
		
		Simulation simulation = modelRepository.getSimulationForUpdate(id);
		setCurrentSelection(simulation);
		if (simulation == null) {
			view.deselectSimulation();
			// Selected simulation does no longer exist in repository, refresh view 
			onRefresh();
		} else {
			view.setCurrentSimulation(simulation, modelRepository.getAllChannelIds(), modelRepository.getChannelsForSimulation(simulation.getId()), namespaces);
		}
	}

	private void setCurrentSelection(Simulation selection) {
		if (selection == null) {
			currentSelection = null;
			namespaces = null;
		} else {
			currentSelection = selection;
			reloadNamespaces();
		}
	}

	@Override
	public void onRemove(String id) {
		modelRepository.deleteSimulation(id);
		view.deselectSimulation();
		view.setSelection(null);
		setCurrentSelection(null);
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
				setCurrentSelection(simulation);
				view.setCurrentSimulation(simulation, modelRepository.getAllChannelIds(), modelRepository.getChannelsForSimulation(simulationId), namespaces);
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
		setCurrentSelection(null);
		view.setSimulationList(simulations);
	}

	@Override
	public void onSave() {
		if (currentSelection != null) {
			currentSelection.getNamespaceResolver().setPrefixes(namespaces);
			modelRepository.insertSimulation(currentSelection, true, view.getChannelAssignments());
			
			view.deselectSimulation();
			view.setSelection(null);
			setCurrentSelection(null);
		}
	}

	@Override
	public void onRemoveNamespacePrefix(String prefix) {
		currentSelection.getNamespaceResolver().removePrefix(prefix);
		reloadNamespaces();
		view.setNamespaces(namespaces);
	}

	@Override
	public void onAddNamespacePrefix() {
		NamespaceResolver namespaceResolver = currentSelection.getNamespaceResolver();
		Map<String, String> namespaces = namespaceResolver.getKnownNamespaces();
		int i = 0;
		while (namespaces.containsKey(nsKey(i))) {
			i++;
		}
		namespaces.put(nsKey(i), DEFAULT_URI);
		
		editNamespacePrefix(nsKey(i), DEFAULT_URI);
	}

	private void editNamespacePrefix(String pfx, String uri) {
		EditNamespacePrefixViewImpl pfxView = new EditNamespacePrefixViewImpl(parent);
		pfxView.show(this, pfx, uri);
	}

	private static String nsKey(int i) {
		return "ns" + i;
	}

	@Override
	public void onEditNamespacePrefix(String prefix, String uri) {
		editNamespacePrefix(prefix, uri);
	}

	@Override
	public void onPrefixUpdated(String originalPrefix, String prefix, String uri) {
		currentSelection.getNamespaceResolver().addPrefix(prefix, uri);
		if (!originalPrefix.equals(prefix)) {
			currentSelection.getNamespaceResolver().removePrefix(originalPrefix);
		}
		reloadNamespaces();
		view.setNamespaces(namespaces);
	}

	private void reloadNamespaces() {
		namespaces = currentSelection.getNamespaceResolver().getKnownNamespaces();
	}

	@Override
	public boolean isPrefixAvailable(String pfx) {
		return currentSelection.getNamespaceResolver().resolveNamespacePrefix(pfx) == null;
	}
}
