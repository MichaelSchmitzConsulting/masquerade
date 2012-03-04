package masquerade.sim.run;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import run.Main;

/**
 * Contains the logic to run Masquerade as a webapp on an embedded jetty server.
 * Separated into an own class to avoid dependencies on Jetty in {@link Main}
 * as the necessary jars will only be available after the WAR has been
 * unpacked to a temporary directory.  
 */
public class ServerRunner {
	public static void runServer(File webappRoot, int port) throws Exception, InterruptedException {
		Server server = new Server(port);
		
		String unpackDir = webappRoot.getAbsolutePath();
		
		WebAppContext webapp = new WebAppContext();
        webapp.setDescriptor(unpackDir + "/WEB-INF/web.xml");
        webapp.setResourceBase(unpackDir);
        webapp.setContextPath("/");
        webapp.setParentLoaderPriority(true);
        server.setHandler(webapp);
		
		server.start();
		server.join();
	}
}
