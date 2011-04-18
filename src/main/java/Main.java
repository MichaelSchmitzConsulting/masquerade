import java.net.URL;
import java.security.CodeSource;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Standalone Masquerade runner using an embedded Jetty to launch the simulator.
 * 
 * <p>Supported system properties:</p>
 * <ul>
 * <li>masquerade.port: TCP Port to bind HTTP server to (default: 8888)</li>
 * <li>masquerade.db.file.location: Location of the database file</li>
 * <li>masquerade.request.log.dir: Directory where request history payload is stored</li>
 * </ul>
 */
public class Main {

	private int port;

	public Main(int port) {
		this.port = port;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static int main(String[] args) throws Exception {
		int port = Integer.parseInt(System.getProperty("masquerade.port", "8888"));
		
		Main main = new Main(port);
		return main.run();
	}

	/**
	 * Starts masquerade in an embedded Jetty container
	 * @return 0 if succesful, -1 if unable to determine war location
	 * @throws Exception
	 */
	private int run() throws Exception {
		String warPath = getWarPath();
		if (warPath == null) {
			System.err.println("Unable to determine WAR file this application was loaded from");
			return -1;
		}
		
		Server server = new Server(port);
		
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/masquerade");
		webapp.setWar(warPath);
		server.setHandler(webapp);
		
		server.start();
		server.join();
		
		return 0;
	}

	/**
	 * @return Path to the WAR file this application was loaded from, or <code>null</code> if not found
	 */
	private String getWarPath() {
		CodeSource source = getClass().getProtectionDomain().getCodeSource();
		if (source != null) {
			URL location = source.getLocation();
			return location.getPath();
		} else {
			return null;
		}
	}
}
