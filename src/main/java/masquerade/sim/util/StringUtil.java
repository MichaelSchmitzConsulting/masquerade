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
	
	public static String removeTrailingSlash(String str) {
		if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '/') {
			return str.substring(0, str.length() - 1);
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
			if (value != null) {
				ret = content.replace("${" + name + "}", value);
			}
		}
		return ret;
	}

	public static String fromCamelCase(String uqn) {
		StringBuffer name = new StringBuffer(uqn);
		if (name.length() == 0) return "";
		
		// Convert first character to upper case
		char low = name.charAt(0);
		char upper = Character.toUpperCase(low);
		name.setCharAt(0, upper);
		
		int shift = 0;
		for (int i = 1; i < uqn.length(); ++i) {
			if (StringUtil.isUpperCase(uqn, i) && !StringUtil.isUpperCase(uqn, i + 1)) {
				name.insert(i + shift, ' ');
				shift++;
			}
		}
		return name.toString();
	}

	public static boolean isUpperCase(String uqn, int i) {
		return i < uqn.length() && Character.isUpperCase(uqn.charAt(i));
	}
}
