package masquerade.sim.model.impl;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import masquerade.sim.channel.ftp.FtpChannelListener;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ChannelListener;

/**
 * A {@link Channel} receiving requests from 
 */
public class FtpChannel extends AbstractChannel {

	private String host = "localhost";
	private int port = 21;
	private String remoteDirectory = "";
	private String remoteFileName = "";
	private boolean deleteRemoteFileAfterRetrieval = true;
	private long pollingPeriodMs = 60000;
	private String username = "anonymous";
	private String password = "";
	
	public FtpChannel(String name) {
		super(name);
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the remoteDirectory
	 */
	public String getRemoteDirectory() {
		return remoteDirectory;
	}

	/**
	 * @param remoteDirectory the remoteDirectory to set
	 */
	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

	/**
	 * @return the remoteFileName
	 */
	public String getRemoteFileName() {
		return remoteFileName;
	}

	/**
	 * @param remoteFileName the remoteFileName to set
	 */
	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}

	/**
	 * @return the pollingPeriodMs
	 */
	public long getPollingPeriodMs() {
		return pollingPeriodMs;
	}

	/**
	 * @param pollingPeriodMs the pollingPeriodMs to set
	 */
	public void setPollingPeriodMs(long pollingPeriodMs) {
		this.pollingPeriodMs = pollingPeriodMs;
	}

	/**
	 * @return the deleteRemoteFileAfterRetrieval
	 */
	public boolean isDeleteRemoteFileAfterRetrieval() {
		return deleteRemoteFileAfterRetrieval;
	}

	/**
	 * @param deleteRemoteFileAfterRetrieval the deleteRemoteFileAfterRetrieval to set
	 */
	public void setDeleteRemoteFileAfterRetrieval(boolean deleteRemoteFileAfterRetrieval) {
		this.deleteRemoteFileAfterRetrieval = deleteRemoteFileAfterRetrieval;
	}

	@Override
	public boolean isActive() {
		return isNotEmpty(host) && isNotEmpty(remoteDirectory) && isNotEmpty(remoteFileName) && pollingPeriodMs > 5000; 
	}

	@Override
	public Class<? extends ChannelListener<? extends Channel>> listenerType() {
		return FtpChannelListener.class;
	}
}
