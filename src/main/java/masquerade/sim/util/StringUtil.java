package masquerade.sim.util;

public class StringUtil {

	public static String removeLeadingSlash(String str) {
		if (str != null && str.length() > 0 && str.charAt(0) == '/') {
			return str.substring(1);
		} else {
			return str;
		}
	}

}
