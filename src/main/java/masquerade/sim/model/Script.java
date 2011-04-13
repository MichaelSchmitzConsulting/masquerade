package masquerade.sim.model;



/**
 * A simulation script definition
 */
public interface Script {
	String getName();
	String getDescription();

	/**
	 * Run this script, returning a response object
	 * @param request The request object
	 * @param converter A converter that converts objects (variables, requests, ...) to desired types
	 * @param fileLoader A {@link FileLoader} used to load templates
	 * @return A response object
	 * @throws Exception
	 */
	public Object run(Object request, Converter converter, FileLoader fileLoader) throws Exception;
}
