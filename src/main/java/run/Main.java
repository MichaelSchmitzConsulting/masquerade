package run;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;

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
	 * Entry point. See class documentation for arguments.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		int port = Integer.parseInt(System.getProperty("masquerade.port", "8888"));
		
		Main main = new Main(port);
		main.run();
	}

	/**
	 * Starts masquerade in an embedded Jetty container
	 * @return 
	 * @return 0 if succesful, -1 if unable to determine war location
	 * @throws Exception
	 */
	private boolean run() throws Exception {
		String warPath = getWarPath();
		if (warPath == null) {
			System.err.println("Unable to determine WAR file this application was loaded from");
			return false;
		}
		
		File unpackDir = unpack(warPath);
		if (unpackDir == null) {
			System.err.println("Unable to unpack WAR file to temporary folder");
			return false;
		}
		
		ClassLoader serverRunnerLoader = createClassLoader(new File(unpackDir, "WEB-INF/lib"));
		Thread.currentThread().setContextClassLoader(serverRunnerLoader);
		
		try {
			runServer(serverRunnerLoader, unpackDir);
		} finally {
			deleteDirectory(unpackDir, serverRunnerLoader);
		}
		
		return true;
	}

	private void deleteDirectory(File unpackDir, ClassLoader serverRunnerLoader) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Class<?> fileUtils = serverRunnerLoader.loadClass("org.apache.commons.io.FileUtils");
		Method deleteDir = fileUtils.getMethod("deleteDirectory", File.class);
		System.out.println("Removing temporary directory " + unpackDir.getAbsolutePath());
		deleteDir.invoke(null, unpackDir);
	}

	/**
	 * Runs the {@link ServerRunner}, loading it using reflection to avoid dependency from this class
	 * ServerRunner as the class loader for this class will be unable to load Jetty.
	 * @param serverRunnerLoader 
	 * @param unpackDir Where the WAR has been exploded to
	 */
	private void runServer(ClassLoader serverRunnerLoader, File unpackDir) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, MalformedURLException {
		Class<?> runner = serverRunnerLoader.loadClass("run.ServerRunner");
		Method runServer = runner.getMethod("runServer", File.class, int.class);
		System.out.println("Starting Masquerade Standalone on port " + port);
		runServer.invoke(null, unpackDir, port);
	}

	/**
	 * Creates a {@link ClassLoader} for loading Jetty, includes all JARs in 
	 * the webapp's lib dir
	 */
	private ClassLoader createClassLoader(File libdir) throws MalformedURLException {
		File[] jarFiles = libdir.listFiles(new FilenameFilter() {
			@Override public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		URL[] urls = new URL[jarFiles.length];
		int i = 0;
		for (File jar : jarFiles) {
			System.out.println("Including JAR: " + jar);
			urls[i++] = jar.toURI().toURL();
		}
		return new URLClassLoader(urls);
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

	/**
	 * Unpacks the WAR archive at the specified directory to a temporary directory
	 * @param warPath 
	 * @return The root webapp directory of the exploded WAR
	 * @throws IOException
	 */
	private static File unpack(String warPath) throws IOException {
		File dir = createTempDir();
		if (dir == null) {
			return null;
		}
		
		Zippy.unzip(warPath, dir);
		
		return dir;
	}

	/**
	 * Creates a temporary directory suitable to explode the WAR intos
	 * @return The temp. dirs
	 * @throws IOException
	 */
	private static File createTempDir() throws IOException {
		File temp = File.createTempFile("masq", null);
		temp.deleteOnExit();
		File tempDir = new File(temp.getParentFile(), temp.getName() + "-war");

		if (!tempDir.mkdir()) {
			System.err.println("Cannot create directory " + tempDir.getAbsolutePath());
			return null;
		}
	
		return tempDir;
	}
}
