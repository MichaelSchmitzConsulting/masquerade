package masquerade.sim.model;

import masquerade.sim.model.impl.step.AbstractSimulationStep;

public class EchoStep extends AbstractSimulationStep {

	public EchoStep(String name) {
		super(name);
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Object request = context.getRequest(Object.class);
		context.setContent(request);
	}

}