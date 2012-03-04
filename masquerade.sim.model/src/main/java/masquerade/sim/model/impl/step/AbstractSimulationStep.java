package masquerade.sim.model.impl.step;

import masquerade.sim.model.SimulationStep;

/**
 * {@link SimulationStep} base providing common properties
 */
public abstract class AbstractSimulationStep implements SimulationStep {

	private String name;
	
	public AbstractSimulationStep(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return toString();
	}
}
