package masquerade.sim.model.impl.step;

import masquerade.sim.model.SimulationContext;

public class WaitStep extends AbstractSimulationStep {

	private int waitMillis = 0;
	
	public WaitStep(String name) {
		super(name);
	}

	public WaitStep() {
	}

	public int getWaitMillis() {
		return waitMillis;
	}

	public void setWaitMillis(int waitMillis) {
		this.waitMillis = waitMillis;
	}

	@Override
	public void execute(SimulationContext context) throws Exception {
		Thread.sleep(waitMillis);
	}

	@Override
	public String toString() {
		return "WaitStep (" + waitMillis + " ms)";
	}
}
