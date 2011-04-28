package masquerade.sim.model;

import java.io.InputStream;
import java.util.Map;

/**
 * Context available to {@link SimulationStep}  in {@link SimulationStep#execute(SimulationContext)}.
 * Contains simulation variables, request and response content, a {@link NamespaceResolver}
 * and the possibility to load files (see {@link FileType}).
 */
public interface SimulationContext extends Converter {
	/**
	 * Returns the request's content. Please note that some steps might simply set the request
	 * as the response content. If the response content is then altered, the original request
	 * will be changed as well as it references the same object.
	 * 
	 * @param <R> Expected return type
	 * @param expectedType Expected return type class
	 * @return The request converted to the expected type, or <code>null</code> if conversion not available
	 */
	<R> Object getRequest(Class<R> expectedType);
	
	/**
	 * Returns the response content in its current state as set by previous simulations steps.
	 * Initially set to <code>null</code>.
	 * 
	 * @param <C> Expected return type
	 * @param expectedType Expected return type class
	 * @return The response content converted to the expected type, or <code>null</code> if conversion not available or if value is not yet set
	 */
	<C> C getContent(Class<C> expectedType);
	/**
	 * Set the response content to a new value
	 * @param content New response content
	 */
	void setContent(Object content);
	
	/**
	 * Sets a variable on this context, available to all following steps. Useful for 
	 * scripted steps accessing variables or variable substitution in text.
	 * @param name Variable name
	 * @param value Variable vaule
	 */
	void setVariable(String name, Object value);
	/**
	 * Reuturns a variable's value if set
	 * @param <T> Variable type
	 * @param name Variable name
	 * @return Variable value, or <code>null</code> if not set
	 */
	<T> T getVariable(String name);
	/**
	 * Check if a variable with the specified name is set
	 * @param name Variable name
	 * @return <code>true</code> if this variable is available in the context
	 */
	boolean hasVariable(String name);
	/**
	 * @return A copy of the {@link Map} containing all variables in this context
	 */
	Map<String, Object> getVariables();
	
	/**
	 * Substitutes all variables known in this context. Leaves unknown variables as
	 * they are (does not substitute them with the empty string). Variable values
	 * are converted to String using a {@link Converter} (which falls back to {@link Object#toString()}
	 * if no converter for the variable type is registered).
	 * @param content Text to substitute variables in
	 * @return Text with variables substituted
	 */
	String substituteVariables(String content);
	
	/**
	 * @return A {@link NamespaceResolver} able to resolve all namespaces registered in the model.
	 */
	NamespaceResolver getNamespaceResolver();
	
	/**
	 * Loads a file (e.g. a template) of the given name
	 * @param type
	 * @param name
	 * @return {@link InputStream} for the file, or <code>null</code> if not found
	 */
	InputStream load(FileType fileType, String fileName);
}
