package masquerade.sim.channel.http.impl;

import javax.servlet.http.HttpServletRequest;

import masquerade.sim.util.StringUtil;

/**
 * Code shared by all HTTP channel implementations.
 */
class HttpUtil {

	/**
	 * Removes the leading slash from a request URL
	 * @param pathInfo
	 * @return
	 */
	static String requestUrlName(String pathInfo) {
		if (pathInfo == null || pathInfo.length() == 0) {
			return null;
		}
		
		return StringUtil.removeLeadingSlash(pathInfo);
	}

	static String clientInfo(HttpServletRequest req) {
		return req.getRemoteAddr() + ":" + req.getRemotePort();
	}

}
