package masquerade.sim.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import masquerade.sim.model.Converter;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;

/**
 * Holds global configuration variables, provides
 * them to simulation invocations and handles
 * changes to these global vars via settings.
 */
public class ConfigurationVariableHolder implements VariableHolder {

	private final static StatusLog log = StatusLogger.get(ConfigurationVariableHolder.class);

	private volatile Map<String, Object> vars;

	private final Converter converter;
	
	/**
	 * @param converter
	 */
	public ConfigurationVariableHolder(Converter converter) {
		this.converter = converter;
		this.vars = new HashMap<String, Object>(cast(System.getProperties()));
	}

	/**
	 * Parses config variables in {@link Properties} format
	 * @param propertiesStr A String conforming to the {@link Properties} format
	 */
	public void consumeConfigurationVariables(String propertiesStr) {
		Properties props = new Properties();
		
		if (propertiesStr != null) {
			try {
				props.load(new StringReader(propertiesStr));
			} catch (IOException e) {
				log.error("Unable to read properties string", e);
			}
		}
		
		vars = merge(cast(System.getProperties()), cast(props));
	}

	private static Map<String, Object> merge(Map<String, Object> sysProps, Map<String, Object> props) {
		Map<String, Object> all = new HashMap<String, Object>(sysProps);
		all.putAll(props);
		return all;
	}

	@Override
	public Map<String, Object> getVariables() {
		return Collections.unmodifiableMap(vars);
	}

	@Override
	public String substituteVariables(String content) {
		return StringUtil.substituteVariables(vars, content, converter);
	}

	/**
	 * Casts properties (which is defined as a Map<Object, Object>) to a Map<String, Object>.
	 * Configuration properties are String->String mappings, so this cast is safe. 
	 */
	private static Map<String, Object> cast(Properties props) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String, Object> propsMap = (Map) props;
		return propsMap;
	}

}
