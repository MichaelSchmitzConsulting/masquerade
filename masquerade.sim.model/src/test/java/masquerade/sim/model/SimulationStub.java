package masquerade.sim.model;

public class SimulationStub implements Simulation {

	private final String id;
	
	public SimulationStub(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public RequestMapping<?> getSelector() {
		return null;
	}

	@Override
	public RequestIdProvider<?> getRequestIdProvider() {
		return null;
	}

	@Override
	public Script getScript() {
		return null;
	}

	@Override
	public NamespaceResolver getNamespaceResolver() {
		return null;
	}
}
