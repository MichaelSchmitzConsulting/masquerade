package masquerade.sim.model;

import masquerade.sim.model.impl.step.AbstractSimulationStep;

public class CopyRequestToResponseStep extends AbstractSimulationStep {

	public CopyRequestToResponseStep() {
		super("step");
	}

	public CopyRequestToResponseStep(String name) {
		super(name);
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Object request = context.getRequest(Object.class);
		context.setContent(request);
	}

	@Override
	public String toString() {
		return "Copy Request Payload To Response Content";
	}
}
