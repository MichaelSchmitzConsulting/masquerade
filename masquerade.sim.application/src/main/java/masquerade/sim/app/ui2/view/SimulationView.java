package masquerade.sim.app.ui2.view;

import java.util.List;

import masquerade.sim.model.Simulation;

/**
 * View allowing to view and edit {@link Simulation}s.
 */
public interface SimulationView {
	void setSimulationList(List<String> simulations);
	void setCurrentSimulation(Simulation simulation);
	void setSelection(String id);
	
	interface SimulationViewCallback {
		void onSimulationSelected(String id);
		void onRemove(String id);
		void onAdd();
		void onRefresh();
		void onSave();
	}
}
