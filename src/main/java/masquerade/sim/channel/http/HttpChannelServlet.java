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

import masquerade.sim.ApplicationContext;
import masquerade.sim.ApplicationLifecycle;
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
		Map<String, String[]> map = req.getParameterMap();
		for (Map.Entry<String, String[]> param : map.entrySet()) {
			String name = param.getKey();
			str.append('<').append(name).append('>');
			String[] values = param.getValue();
			str.append(StringUtil.join(values));
			str.append("</").append(name).append('>');
		}
		str.append("</GetRequest>");
		
		doRequest(req, resp, new ByteArrayInputStream(str.toString().getBytes()));
	}
	
	private void doRequest(HttpServletRequest req, HttpServletResponse resp, InputStream content) throws ServletException {
		HttpChannelListener listener  = findChannelListener(req.getPathInfo());
		if (listener != null) {
			processWithChannel(HttpUtil.clientInfo(req), resp, content, listener);
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private static void processWithChannel(String clientInfo, HttpServletResponse resp, InputStream content, HttpChannelListener channelListener) throws ServletException {
		try {
			ServletOutputStream outputStream = resp.getOutputStream();
			resp.setContentType(channelListener.getContentType());
			channelListener.processRequest(clientInfo, content, outputStream);
			outputStream.close();
		} catch (Exception e) {
			throw new ServletException("Error processing request", e);
		}
	}

	private HttpChannelListener findChannelListener(String pathInfo) {
		String requestUrl = HttpUtil.requestUrlName(pathInfo);
		if (requestUrl == null) {
			return null;
		}
		
		ApplicationContext context = ApplicationLifecycle.getApplicationContext(getServletContext());
		Collection<HttpChannelListener> allListeners = context.getChannelListenerRegistry().getAllListeners(HttpChannelListener.class);
		for (HttpChannelListener listener : allListeners) {
			if (listener.locationMatches(requestUrl)) {
				return listener;
			}
		}
		return null;
	}
}
