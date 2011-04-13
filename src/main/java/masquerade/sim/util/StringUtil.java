package masquerade.sim.util;

import java.util.Map;

import masquerade.sim.model.Converter;

public class StringUtil {

	public static String removeLeadingSlash(String str) {
		if (str != null && str.length() > 0 && str.charAt(0) == '/') {
			return str.substring(1);
		} else {
			return str;
		}
	}

	public static String substituteVariables(Map<String, Object> contextVariables, String content, Converter converter) {
		String ret = content;
		// TODO: Better implementation, e.g. with StringTemplate
		for (Map.Entry<String, Object> entry : contextVariables.entrySet()) {
			String name = entry.getKey();
			String value = converter.convert(entry.getValue(), String.class);
			
			ret = content.replace("${" + name + "}", value);
		}
		return ret;
	}

}
