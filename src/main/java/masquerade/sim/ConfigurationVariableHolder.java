package masquerade.sim;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import masquerade.sim.model.VariableHolder;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * Holds global configuration variables, provides
 * them to simulation invocations and handles
 * changes to these global vars via settings.
 */
public class ConfigurationVariableHolder implements VariableHolder {

	private final static StatusLog log = StatusLogger.get(ConfigurationVariableHolder.class);

	private Map<String, Object> vars = Collections.emptyMap();
	
	/**
	 * Parses config variables in {@link Properties} format
	 * @param propertiesStr A String conforming to the {@link Properties} format
	 */
	public void consumeConfigurationVariables(String propertiesStr) {
		Properties props = new Properties();
		try {
			props.load(new StringReader(propertiesStr));
			vars = new HashMap<String, Object>(cast(props));
		} catch (IOException e) {
			log.error("Unable to read properties string", e);
		}
	}

	@Override
	public Map<String, Object> getVariables() {
		return Collections.unmodifiableMap(vars );
	}

	private static Map<String, Object> cast(Properties props) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Object> propsMap = (Map) props;
		return propsMap;
	}

}
