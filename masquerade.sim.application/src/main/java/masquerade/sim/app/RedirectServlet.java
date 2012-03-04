package masquerade.sim.app;

import static masquerade.sim.app.UrlConstants.APP_REDIRECT;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * A servlet redirecting from / to /app
 */
@SuppressWarnings("serial")
@Component(name="redirectServlet")
@Service(Servlet.class)
public class RedirectServlet extends HttpServlet {
	private static final String ROOT = "/";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (ROOT.equals(req.getPathInfo())) {
			resp.sendRedirect(APP_REDIRECT);
		}
	}
}
