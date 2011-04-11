package masquerade.sim.model;



/**
 * A simulation script definition
 */
public interface Script {
	String getName();
	String getDescription();
	
	/**
	 * 
	 * @param request
	 * @return Response object
	 * @throws Exception
	 */
	Object run(Object request) throws Exception;
}
