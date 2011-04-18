package masquerade.sim.run;

import java.net.URL;
import java.security.CodeSource;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Standalone Masquerade runner using an embedded Jetty to launch the simulator.
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
