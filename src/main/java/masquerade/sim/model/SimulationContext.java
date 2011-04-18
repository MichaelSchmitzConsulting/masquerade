package masquerade.sim.model;

import java.io.InputStream;
import java.util.Map;

public interface SimulationContext extends Converter {
	<R> Object getRequest(Class<R> expectedType);
	
	<C> C getContent(Class<C> expectedType);
	void setContent(Object content);
	
	void setVariable(String name, Object value);
	<T> T getVariable(String name);
	boolean hasVariable(String name);
	Map<String, Object> getVariables();
	
	String substituteVariables(String content);
	
	NamespaceResolver getNamespaceResolver();
	
	/**
	 * Loads a file (e.g. a template) of the given name
	 * @param type
	 * @param name
	 * @return {@link InputStream} for the file, or <code>null</code> if not found
	 */
	InputStream load(FileType fileType, String fileName);
}
