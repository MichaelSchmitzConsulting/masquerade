package masquerade.sim.core.api;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * Template for common Servlet response operations 
 */
public class ResponseTemplate {

	/**
	 * Sets response error code 400 and returns a provided error message
	 * @param resp
	 * @throws IOException
	 */
	public static void errorResponse(HttpServletResponse resp, String errorMessage) throws IOException {
		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(resp.getOutputStream()));
		writer.println(errorMessage);
		writer.flush();
		writer.close();
	}

}
