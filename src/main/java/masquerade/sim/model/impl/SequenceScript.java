package masquerade.sim.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;

/**
 * A response simulation script executing one step after each other 
 */
public class SequenceScript extends AbstractScript {

	private List<SimulationStep> simulationSteps;

	public SequenceScript(String name) {
		super(name);
		this.simulationSteps = new ArrayList<SimulationStep>();
	}
	
	@Override
	public Object run(SimulationContext context) throws Exception {
		for (SimulationStep step : simulationSteps) {
			step.execute(context);
		}
		
		return context.getContent(Object.class);
	}
	
	/**
	 * @return The {@link SimulationStep steps} contained in this script 
	 */
	public List<SimulationStep> getSimulationSteps() {
		return Collections.unmodifiableList(simulationSteps);
	}
	
	/**
     * @param simulationSteps Replaces the simulation step sequence for this scripts
     */
    public void setSimulationSteps(List<SimulationStep> simulationSteps) {
    	this.simulationSteps = new ArrayList<SimulationStep>(simulationSteps);
    }

	public void addSimulationStep(SimulationStep step) {
		simulationSteps.add(step);
    }

	@Override
	public String toString() {
		return getName() + " (" + simulationSteps.size() + " steps)";
	}

}
