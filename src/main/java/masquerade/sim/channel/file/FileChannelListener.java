package masquerade.sim.channel.file;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;

import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.FileChannel;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

public class FileChannelListener extends AbstractChannelListener<FileChannel> {

	private static final long MAX_WAIT_TIME = 20000;
	private static final StatusLog log = StatusLogger.get(FileChannelListener.class);
	
	private String directory;
	private String moveToDirectory;
	private String filenameRegex;
	private long pollingPeriod;

	private Thread filePollerThread;
	
	@Override
	protected synchronized void onStart(FileChannel channel) {
		directory = channel.getDirectory();
		filenameRegex = channel.getFilenameRegex();
		pollingPeriod = channel.getPollingPeriodSeconds();
		moveToDirectory = channel.getMoveToDirectory();
		startFilePoller();
	}

	@Override
	protected synchronized void onStop() {
		stopFilePoller();
	}

	private void startFilePoller() {
		stopFilePoller();
		if (isNotEmpty(directory) && isNotEmpty(filenameRegex)) {
			filePollerThread = new FilePollingThread(directory, moveToDirectory, filenameRegex, pollingPeriod, createFileProcessor());
		} else {
			log.warning("Cowardly refusing to start file polling thread, directory and filename regex must both be specified");
		}
	}

	private FileProcessor createFileProcessor() {
		return new FileProcessor() {
			@Override public void processFile(File file) throws Exception {
				String content = IOUtils.toString(new FileInputStream(file));
				OutputStream devNull = new NullOutputStream(); // TODO: Response handling?
				Date requestTimestamp = new Date(file.lastModified());
				processRequest("FileChannel " + directory, content, devNull, requestTimestamp);
			}
		};
	}

	private void stopFilePoller() {
		if (filePollerThread != null) {
			filePollerThread.interrupt();
			try {
				filePollerThread.join(MAX_WAIT_TIME);
			} catch (InterruptedException e) {
				log.warning("Interrupted while waiting for file poller thread to stop");
			}
			filePollerThread = null;
		}
	}
}
