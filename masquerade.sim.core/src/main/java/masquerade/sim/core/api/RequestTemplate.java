package masquerade.sim.core.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common request-related logic
 */
public class RequestTemplate {
	public static String pathMatches(String pathInfo, Pattern pattern) {
		if (pathInfo != null) {
			Matcher matcher = pattern.matcher(pathInfo);
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return null;
	}

}
