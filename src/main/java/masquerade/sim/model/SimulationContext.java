package masquerade.sim.model;

import java.io.InputStream;

public interface SimulationContext {
	<R> R getContent(Class<R> expectedType);
	void setContent(Object content);
	
	void setVariable(String name, Object value);
	<T> T getVariable(String name);
	
	String substituteVariables(String content);
	
	InputStream load(FileType fileType, String fileName);
	
	<T> T convert(Object value, Class<T> to);
}
