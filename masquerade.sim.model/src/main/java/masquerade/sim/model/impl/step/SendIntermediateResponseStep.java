package masquerade.sim.model.impl.step;

import masquerade.sim.model.SimulationContext;

/**
 * Triggers sending the current response content as an intermediate response
 * if the channel supports it.
 * @see SendIntermediateResponseStep
 */
public class SendIntermediateResponseStep extends AbstractSimulationStep {

	public SendIntermediateResponseStep(String name) {
		super(name);
	}

	public SendIntermediateResponseStep() {
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		context.sendIntermediateResponse();
	}
}
