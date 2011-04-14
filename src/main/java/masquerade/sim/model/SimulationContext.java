package masquerade.sim.model;

import java.io.InputStream;
import java.util.Map;

public interface SimulationContext extends Converter {
	<R> R getContent(Class<R> expectedType);
	void setContent(Object content);
	
	void setVariable(String name, Object value);
	<T> T getVariable(String name);
	Map<String, Object> getVariables();
	
	String substituteVariables(String content);
	
	InputStream load(FileType fileType, String fileName);
}
