package masquerade.sim.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.springframework.util.StopWatch;

/**
 * A response simulation script executing one step after each other 
 */
public class SequenceScript extends AbstractScript {

	private List<SimulationStep> simulationSteps;
	private final static StatusLog log = StatusLogger.get(SequenceScript.class);

	public SequenceScript(String name) {
		super(name);
		this.simulationSteps = new ArrayList<SimulationStep>();
	}
	
	@Override
	public Object run(SimulationContext context) throws Exception {
		StopWatch watch = new StopWatch("Steps");
		for (SimulationStep step : simulationSteps) {
			watch.start("Step: " + step.getName() + " of type " + step.getClass().getName());
			step.execute(context);
			watch.stop();
		}
		log.trace(watch.prettyPrint());
		
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
