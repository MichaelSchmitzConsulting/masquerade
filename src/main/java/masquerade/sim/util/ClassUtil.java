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
		return StringUtil.fromCamelCase(uqn);
    }
}
