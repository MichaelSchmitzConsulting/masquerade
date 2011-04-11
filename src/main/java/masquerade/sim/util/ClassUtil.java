package masquerade.sim.util;

public class ClassUtil {

	public static String unqualifiedName(Object bean) {
    	String name = bean.getClass().getName();
    	return name.substring(name.lastIndexOf('.') + 1, name.length());
    }

	public static String unqualifiedName(Class<?> type) {
    	String name = type.getName();
    	return name.substring(name.lastIndexOf('.') + 1, name.length());
    }

	/**
	 * @param type Class
	 * @return Type unqualified name, first char to upper case, space before an uppercase chars
	 */
	public static String fromCamelCase(Class<?> type) {
		String uqn = unqualifiedName(type);
		StringBuffer name = new StringBuffer(uqn);
		if (name.length() == 0) return "";
		
		// Convert first character to upper case
		char low = name.charAt(0);
		char upper = Character.toUpperCase(low);
		name.setCharAt(0, upper);
		
		int shift = 0;
		for (int i = 1; i < uqn.length(); ++i) {
			if (isUpperCase(uqn, i) && !isUpperCase(uqn, i + 1)) {
				name.insert(i + shift, ' ');
				shift++;
			}
		}
		return name.toString();
    }

	private static boolean isUpperCase(String uqn, int i) {
		return i < uqn.length() && Character.isUpperCase(uqn.charAt(i));
	}

}
