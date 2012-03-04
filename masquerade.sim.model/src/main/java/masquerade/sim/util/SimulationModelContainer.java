package masquerade.sim.util;

import java.util.ArrayList;
import java.util.Collection;

public class SimulationModelContainer {
	private Collection<Object> simulationModelObjects;

	public SimulationModelContainer() {
		simulationModelObjects = new ArrayList<Object>();
	}
		
	public SimulationModelContainer(Collection<? extends Object> modelObjects) {
		this.simulationModelObjects = new ArrayList<Object>(modelObjects);
	}

	public Collection<Object> getSimulationModelObjects() {
		return simulationModelObjects;
	}

	public void setSimulationModelObjects(Collection<Object> simulationModelObjects) {
		this.simulationModelObjects = simulationModelObjects;
	}
}