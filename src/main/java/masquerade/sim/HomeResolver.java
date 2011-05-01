package masquerade.sim;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;

public class HomeResolver {
	private static final String SYSPROP_MASQUERADE_HOME = "masquerade.home";
	private static final String SYSPROP_MASQUERADE_REQUESTLOG_DIR = "masquerade.requestlog.dir";
	private static final String SYSPROP_MASQUERADE_ARTIFACT_DIR = "masquerade.artifact.dir";

	private static final String MSQ_WORKSUBDIR = ".masquerade";
	private static final String SERVLET_WORK_DIR = "javax.servlet.context.tempdir";

	/**
	 * Reads the artifact directory location setting from the system property
	 * <code>masquerade.artifact.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param servletContext {@link ServletContext}
	 * @return Where the masquerade request log directory should be located
	 */
	static File getArtifactsDir(ServletContext servletContext) throws IOException {		
		String artifactDir = System.getProperty(SYSPROP_MASQUERADE_ARTIFACT_DIR);
		File dir;
		if (artifactDir == null) {
			dir = getWorkSubDir(servletContext, "artifact");
		} else {
			dir = new File(artifactDir);
		}
		FileUtils.forceMkdir(dir);
		return dir;
	}

	/**
	 * Reads the masquerade reuest log settings from the system property
	 * <code>masquerade.request.log.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param servletContext {@link ServletContext}
	 * @param postfix 
	 * @return Where the masquerade database should be located
	 */
	static File getDbFileLocation(ServletContext servletContext, DbType dbType) {
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
		
		File home = getMasqueradeHome(servletContext);
		File dbFile = new File(home, dbFileBase + "-db.db4o");
		return dbFile;
	}

	/**
	 * The app's home directory as determined by 
	 * 
	 * <ol>
	 * <li>the system propery<code>masquerade.home</code></li>
	 * <li>${user.home}/.masquerade/&lt;webapp-context-path&gt;</li>
	 * <li>the servlet context attribute <code>javax.servlet.context.tempdir</code></li>
	 * </ol>
	 * 
	 * @param servletContext
	 * @return Location of the working directory
	 */
	private static File getMasqueradeHome(ServletContext servletContext) {
		String home = System.getProperty(SYSPROP_MASQUERADE_HOME);
		File workDir;
		if (home != null) {
			workDir = new File(home);
		} else {
			File userDir = FileUtils.getUserDirectory();
			File baseDir = new File(userDir, MSQ_WORKSUBDIR);
			workDir = new File(baseDir, getAppDirName(servletContext));
			if (!userDir.exists() || (!workDir.exists() && !workDir.mkdirs())) {
				File servletDir = (File) servletContext.getAttribute(SERVLET_WORK_DIR);
				workDir = new File(servletDir, MSQ_WORKSUBDIR);
				if (!workDir.exists() && !workDir.mkdirs()) {
					throw new IllegalStateException("Cannot create work directory in user.home or javax.servlet.context.tempdir");
				}
			}
		}
		
		return workDir;
	}

	/**
	 * Reads the masquerade reuest log location setting from the system property
	 * <code>masquerade.request.log.dir</code>, or places it in the webapp's
	 * work directory if not set.
	 * 
	 * @param servletContext {@link ServletContext}
	 * @return Where the masquerade request log directory should be located
	 */
	static File getRequestLogDir(ServletContext servletContext) throws IOException {
		String requestLogDir = System.getProperty(SYSPROP_MASQUERADE_REQUESTLOG_DIR);
		File dir;
		if (requestLogDir == null) {
			dir = getWorkSubDir(servletContext, "requestlog");
		} else {
			File requestLog = new File(requestLogDir);
			FileUtils.forceMkdir(requestLog.getParentFile());
			dir = requestLog;
		}
		FileUtils.forceMkdir(dir);
		return dir;
	}

	/**
	 * @param servletContext
	 * @param subdir
	 * @return A {@link File} for the specified subdir in the webapp's working directory
	 */
	private static File getWorkSubDir(ServletContext servletContext, String name) {
		File workDir = getMasqueradeHome(servletContext);
		return new File(workDir, name);
	}

	/**
	 * @param servletContext
	 * @return The context path of the webapp suitable for use as a directory name 
	 */
	private static String getAppDirName(ServletContext servletContext) {
		String name = servletContext.getContextPath().replace("/", "_").replace(":", "_").replace("\\", "_"); // TODO: Better implementation
		return name.length() > 0 ? name.substring(1) : "masquerade"; // Remove leading _, set name if deployed at root
	}
}
