package masquerade.sim.model;


/**
 * Maps an incoming request to a {@link ResponseSimulation}
 */
public interface RequestMapping<R> {
	String getName();
	String getDescription();
	
	Script getScript();
	
	Class<R> acceptedRequestType();
	
	/**
	 * 
	 * @param request
	 * @param requestContext 
	 * @return <code>true</code> if the maping should be applied for the given request
	 */
	boolean matches(R request, RequestContext requestContext);
}
