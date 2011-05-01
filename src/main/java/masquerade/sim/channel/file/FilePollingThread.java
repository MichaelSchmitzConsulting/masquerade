package masquerade.sim.channel.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * A {@link Thread} polling for new files matching a specified filename
 */
public class FilePollingThread extends Thread {
	
	private static final StatusLog log = StatusLogger.get(FilePollingThread.class);
	
	private static final int SECOND = 1000;
	
	private File directory;
	private File moveToDirectory;
	private String filenameRegex;
	private long waitingPeriod;
	private FileProcessor fileProcessor;

	private boolean isLogWarning = true;

	/**
	 * @param directory
	 * @param moveToDirectory
	 * @param filenameRegex
	 * @param waitingPeriod
	 * @param fileProcessor
	 */
	public FilePollingThread(String directory, String moveToDirectory, String filenameRegex, long waitingPeriod, FileProcessor fileProcessor) {
		this.directory = new File(directory);
		this.moveToDirectory = new File(moveToDirectory);
		this.filenameRegex = filenameRegex;
		this.waitingPeriod = waitingPeriod;
		this.fileProcessor = fileProcessor;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			if (isReadable(directory) && isReadable(moveToDirectory)) {
				findFiles();
			} else if (isLogWarning ) {
				log.warning("Directory does not exist, is not a directory or is not accessible");
				isLogWarning = false;
			}
			
			doSleep();
		}
	}

	private void findFiles() {
		FilenameFilter filter = new RegexFileFilter(filenameRegex);
		File[] files = directory.listFiles(filter);
		for (File file : files) {
			try {
				fileProcessor.processFile(file);
			} catch (Exception e) {
				log.error("File processing exception", e);
			}
			try {
				FileUtils.moveFileToDirectory(file, moveToDirectory, false);
			} catch (IOException e) {
				log.error("File processing exception while moving file to " + moveToDirectory.getAbsolutePath(), e);
			}
		}
	}

	private void doSleep() {
		try {
			Thread.sleep(waitingPeriod * SECOND);
		} catch (InterruptedException e) {
			interrupt();
		}
	}
	
	private static boolean isReadable(File directory) {
		return directory.exists() && directory.isDirectory() && directory.canRead();
	}
}
