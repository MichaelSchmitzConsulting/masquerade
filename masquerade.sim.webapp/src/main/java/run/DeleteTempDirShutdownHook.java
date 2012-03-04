package run;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * {@link Runtime} shutdown hook to delete temporary directory upon 
 * shutdown.
 */
public class DeleteTempDirShutdownHook extends Thread {
	private File dir;
	private ClassLoader loader;

	public DeleteTempDirShutdownHook(ClassLoader loader, File dir) {
		this.dir = dir;
		this.loader = loader;
	}

	@Override
	public void run() {
		try {
			deleteDir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete temporary directory using FileUtils#deleteDirectory
	 */
	private void deleteDir() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?> fileUtils = loader.loadClass("org.apache.commons.io.FileUtils");
		Method deleteDir = fileUtils.getMethod("deleteDirectory", File.class);
		System.out.println("Removing temporary directory " + dir.getAbsolutePath());
		deleteDir.invoke(null, dir);
	}
}
