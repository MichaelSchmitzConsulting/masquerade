package masquerade.sim.model;


/**
 * Describes how a response is simulated for an incoming request. Simulation 
 * behavior is modelled in a {@link Script}.
 */
public class ResponseSimulation {
	
	private String name;
	private String description = "";
	private RequestIdProvider<?> requestIdProvider = null;
	private Script script;
	
	public ResponseSimulation(String name) {
		this.name = name;
	}

	public RequestIdProvider<?> getRequestIdProvider() {
		return requestIdProvider;
	}

	public void setRequestIdProvider(RequestIdProvider<?> requestIdProvider) {
		this.requestIdProvider = requestIdProvider;
	}

	public String getName() {
		return name;
	}
	
	/**
     * @param description Short description of this response simulation
     */
    public void setDescription(String description) {
    	this.description = description;
    }

	public String getDescription() {
		return description;
	}

	public Script getScript() {
		return script;
	}

	/**
     * @param script the script to set
     */
    public void setScript(Script script) {
    	this.script = script;
    }

	@Override
	public String toString() {
		return name;
	}	
}
