package masquerade.sim.util;

public class ClassUtil {

	/**
	 * @param type Class
	 * @return Type unqualified name, first char to upper case, space before an uppercase chars
	 */
	public static String fromCamelCase(Class<?> type) {
		String uqn = type.getSimpleName();
		return StringUtil.fromCamelCase(uqn);
    }
}
