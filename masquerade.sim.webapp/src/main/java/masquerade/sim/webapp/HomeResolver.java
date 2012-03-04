package masquerade.sim.webapp;

import java.io.File;
import java.io.IOException;

public class HomeResolver {
	private static final String SYSPROP_MASQUERADE_HOME = "masquerade.home";
	private static final String SYSPROP_MASQUERADE_REQUESTLOG_DIR = "masquerade.requestlog.dir";
	private static final String SYSPROP_MASQUERADE_ARTIFACT_DIR = "masquerade.artifact.dir";
	private static final String SYSPROP_MASQUERADE_PLUGIN_DIR = "masquerade.plugin.dir";
	private static final String SYSPROP_MASQUERADE_PLUGIN_CACHE_DIR = "masquerade.plugin.cache.dir";

	private static final String MSQ_WORKSUBDIR = ".masquerade";
	
	private String appDirName;

	public HomeResolver(String appDirName) {
		this.appDirName = appDirName;
	}
	
	/**
	 * Reads the artifact directory location setting from the system property
	 * <code>masquerade.artifact.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @return Where the masquerade request log directory should be located
	 */
	public File getArtifactRootLocation() throws IOException {		
		String artifactDir = System.getProperty(SYSPROP_MASQUERADE_ARTIFACT_DIR);
		File dir;
		if (artifactDir == null) {
			dir = getHomeSubDir("artifact");
		} else {
			dir = new File(artifactDir);
		}
		createDirectory(dir);
		return dir.getAbsoluteFile();
	}


	/**
	 * Reads the plugin bundle directory location from the system property
	 * <code>masquerade.plugin.dir</code>, or places plugins in ${masquerade.home}
	 * if not set.
	 * 
	 * @return The directory in which plugin bundles are located
	 * @throws IOException If the directory cannot be accessed or created
	 */
	public File getPluginLocation() throws IOException {
		return getDirFromPropertyOrHomeSubdir(SYSPROP_MASQUERADE_PLUGIN_DIR, "plugins");
	}

	/**
	 * Reads the masquerade reuest log settings from the system property
	 * <code>masquerade.request.log.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param postfix 
	 * @return Where the masquerade database should be located
	 */
	public File getDbFileLocation(DbType dbType) {
		String dbFileBase;
		
		switch (dbType) {
		case MODEL:
			dbFileBase = "model";
			break;
		case HISTORY:
			dbFileBase = "history";
			break;
		default:
			throw new IllegalArgumentException(dbType.name());
		}
		
		File home = getMasqueradeHome();
		File dbFile = new File(home, dbFileBase + "-db.db4o");
		return dbFile.getAbsoluteFile();
	}

	/**
	 * Reads the masquerade reuest log location setting from the system property
	 * <code>masquerade.request.log.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @return Where the masquerade request log directory should be located
	 */
	public File getRequestLogDir() throws IOException {
		return getDirFromPropertyOrHomeSubdir(SYSPROP_MASQUERADE_REQUESTLOG_DIR, "requestlog");
	}

	/**
	 * @return The OSGi bundle cache dir, as required in the spec for the OSGi framework
	 * @throws IOException 
	 */
	public File getPludingBundleCacheDir() throws IOException {
		return getDirFromPropertyOrHomeSubdir(SYSPROP_MASQUERADE_PLUGIN_CACHE_DIR, "bundle-cache");
	}

	/**
	 * @param property System property name to read the directory location from
	 * @param homeSubDir masquerade.home subdir name (fallback)
	 * @return Directory from the named system property if set, defaults to the named masquerade.home subdir if the property is not set
	 * @throws IOException
	 */
	private File getDirFromPropertyOrHomeSubdir(String property, String homeSubDir) throws IOException {
		String requestLogDir = System.getProperty(property);
		File dir;
		if (requestLogDir == null) {
			dir = getHomeSubDir(homeSubDir);
		} else {
			File requestLog = new File(requestLogDir);
			createDirectory(requestLog.getParentFile());
			dir = requestLog;
		}
		createDirectory(dir);
		return dir.getAbsoluteFile();
	}

	/**
	 * The app's home directory as determined by 
	 * 
	 * <ol>
	 * <li>the system propery<code>masquerade.home</code></li>
	 * <li>${user.home}/.masquerade/&lt;webapp-context-path&gt;</li>
	 * </ol>
	 * 
	 * @return Location of the working directory
	 */
	private File getMasqueradeHome() {
		String home = System.getProperty(SYSPROP_MASQUERADE_HOME);
		File workDir;
		if (home != null) {
			workDir = new File(home);
		} else {
			File userDir = new File(System.getProperty("user.home"));
			File baseDir = new File(userDir, MSQ_WORKSUBDIR);
			workDir = new File(baseDir, appDirName);
			if (!userDir.exists() || (!workDir.exists() && !workDir.mkdirs())) {
				throw new IllegalStateException("Cannot create work directory in user.home");
			}
		}
		
		return workDir.getAbsoluteFile();
	}

	/**
	 * @param subdir
	 * @return A {@link File} for the specified subdir in the masquerade home directory
	 */
	private File getHomeSubDir(String name) {
		File workDir = getMasqueradeHome();
		return new File(workDir, name).getAbsoluteFile();
	}

	private static void createDirectory(File directory) throws IOException {
		if (directory.exists() && !directory.isDirectory()) {
			throw new IOException(directory.getAbsolutePath() + " exists, but is not a directory");
		}

		directory.mkdirs();
	}
}
