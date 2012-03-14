package masquerade.sim.app.ui2.view;

import java.util.Collection;
import java.util.List;

import masquerade.sim.app.ui2.view.impl.SimulationViewImpl.SimulationInfo;
import masquerade.sim.model.Simulation;

/**
 * View allowing to view and edit {@link Simulation}s.
 */
public interface SimulationView {
	void setSimulationList(List<SimulationInfo> simulations);
	void deselectSimulation();
	void setCurrentSimulation(Simulation simulation, Collection<String> allChannels, Collection<String> assignedToChannel);
	void setSelection(String id);
	Collection<String> getChannelAssignments();
	
	interface SimulationViewCallback {
		void onSimulationSelected(String id);
		void onRemove(String id);
		void onAdd();
		void onSave();
	}
}
