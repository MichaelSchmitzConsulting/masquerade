package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.file.FileChannelListener;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;

/**
 * A channel configuration for polling the file system for new files
 * matching a name specified with a regex.
 */
public class FileChannel extends AbstractChannel {

	private String directory = "";
	private String filenameRegex = "";
	private String moveToDirectory = "";
	private long pollingPeriodSeconds = 60000;
	
	/**
	 * @param name Channel name
	 */
	public FileChannel(String name) {
		super(name);
	}
	
	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return the moveToDirectory
	 */
	public String getMoveToDirectory() {
		return moveToDirectory;
	}

	/**
	 * @param moveToDirectory the moveToDirectory to set
	 */
	public void setMoveToDirectory(String moveToDirectory) {
		this.moveToDirectory = moveToDirectory;
	}

	/**
	 * @return the filenameRegex
	 */
	public String getFilenameRegex() {
		return filenameRegex;
	}

	/**
	 * @param filenameRegex the filenameRegex to set
	 */
	public void setFilenameRegex(String filenameRegex) {
		this.filenameRegex = filenameRegex;
	}

	/**
	 * @return the pollingPeriodSeconds
	 */
	public long getPollingPeriodSeconds() {
		return pollingPeriodSeconds;
	}

	/**
	 * @param pollingPeriodSeconds the pollingPeriodSeconds to set
	 */
	public void setPollingPeriodSeconds(long pollingPeriodSeconds) {
		this.pollingPeriodSeconds = pollingPeriodSeconds;
	}

	/**
	 * @return Whether this configuration contiains a directory and a non-empty regex
	 */
	@Override
	public boolean isActive() {
		return isNotEmpty(directory) && isNotEmpty(filenameRegex) && isNotEmpty(moveToDirectory);
	}

	@Override
	public Class<? extends ChannelListener<? extends Channel>> listenerType() {
		return FileChannelListener.class;
	}

	@Override
	public String toString() {
		return "FileChannel (" + filenameRegex + ")";
	}
}
