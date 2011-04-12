package masquerade.sim.channel.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import masquerade.sim.ApplicationLifecycle;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.model.impl.HttpChannel;
import masquerade.sim.util.StringUtil;

public class HttpChannelServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InputStream content = req.getInputStream();

		doRequest(req, resp, content);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuffer str = new StringBuffer("<GetRequest>");
		@SuppressWarnings("unchecked")
		Map<String, Object> map = req.getParameterMap();
		for (Map.Entry<String, Object> param : map.entrySet()) {
			String name = param.getKey();
			str.append('<').append(name).append('>');
			str.append(param.getValue().toString());
			str.append("</").append(name).append('>');
		}
		str.append("</GetRequest>");
		
		doRequest(req, resp, new ByteArrayInputStream(str.toString().getBytes()));
	}
	
	private void doRequest(HttpServletRequest req, HttpServletResponse resp, InputStream content) throws ServletException {
		ModelRepository repo = ApplicationLifecycle.getApplicationContext(getServletContext()).startModelRepositorySession();
		try {
			doRequest(req, resp, content, repo);
		} finally {
			repo.endSession();
		}
	}

	private static void doRequest(HttpServletRequest req, HttpServletResponse resp, InputStream content, ModelRepository repo) throws ServletException {
		HttpChannel matchingChannel = findChannel(req.getPathInfo(), repo);
		if (matchingChannel != null) {
			processWithChannel(clientInfo(req), resp, content, matchingChannel);
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private static String clientInfo(HttpServletRequest req) {
		return req.getRemoteAddr() + ":" + req.getRemotePort();
	}

	private static void processWithChannel(String clientInfo, HttpServletResponse resp, InputStream content, HttpChannel matchingChannel) throws ServletException {
		try {
			ServletOutputStream outputStream = resp.getOutputStream();
			resp.setContentType(matchingChannel.getContentType());
			matchingChannel.processPost(clientInfo, content, outputStream);
			outputStream.close();
		} catch (Exception e) {
			throw new ServletException("Error processing request", e);
		}
	}

	private static HttpChannel findChannel(String pathInfo, ModelRepository repo) {
		if (pathInfo == null || pathInfo.length() == 0) {
			return null;
		}
		
		pathInfo = StringUtil.removeLeadingSlash(pathInfo);
		
		Collection<HttpChannel> channels = repo.getAll(HttpChannel.class);
		for (HttpChannel channel : channels) {
			String location = StringUtil.removeLeadingSlash(channel.getLocation());
			if (pathInfo.equals(location)) {
				return channel;
			}
		}
		return null;
	}
}
