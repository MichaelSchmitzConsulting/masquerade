package masquerade.sim.channel.ftp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;

import masquerade.sim.model.impl.RequestProcessor;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpReceiverThread extends Thread {

	private static final StatusLog log = StatusLogger.get(FtpReceiverThread.class.getName());
	
	private String host;
	private int port;
	private long pollingPeriodMs;
	private String remoteDirectory;
	private String remoteFileName;
	private String username;
	private String password;
	private RequestProcessor requestProcessor;
	private boolean isDeleteFile;
	
	/**
	 * @param host
	 * @param port
	 * @param pollingPeriodMs
	 * @param remoteDirectory
	 * @param remoteNamePattern
	 * @param remoteFileName
	 * @param username
	 * @param password
	 * @param isDeleteFile 
	 */
	public FtpReceiverThread(RequestProcessor requestProcessor, String host, int port, long pollingPeriodMs, String remoteDirectory, String remoteFileName, String username, String password, boolean isDeleteFile) {
		this.host = host;
		this.port = port;
		this.pollingPeriodMs = pollingPeriodMs;
		this.remoteDirectory = StringUtil.removeTrailingSlash(remoteDirectory);
		this.remoteFileName = remoteFileName;
		this.isDeleteFile = isDeleteFile;
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			transfer();
			try {
				Thread.sleep(pollingPeriodMs);
			} catch (InterruptedException e) {
				interrupt();
			}
		}
	}

	private void transfer() {
		FTPClient ftp = new FTPClient();
		
		try {
			// Connect, log in
			connectAndLogin(ftp);

			if (ftp.isConnected()) {
				// Transfer request file if found
				transferRequestFile(ftp);
				
				// Log out
				ftp.logout();
			}
		} catch (IOException e) {
			log.error("I/O exception while transferring files using FTP from " + hostStr(), e);
		} catch (Exception e) {
			log.error("Exception while processing request from FTP " + hostStr(), e);
		} finally {
			disconnect(ftp);
		}
	}

	private void transferRequestFile(FTPClient ftp) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String filePath = remoteDirectory + "/" + remoteFileName;
		ftp.retrieveFile(filePath, baos);
		String request = new String(baos.toByteArray());

		boolean success = true;
		if (isDeleteFile) {
			if (!ftp.deleteFile(filePath)) {
				log.error("Unable to delete request file on FTP server " + hostStr());
				success = false;
			}
		}
		
		if (success) {
			// TODO: Support responses
			requestProcessor.processRequest(hostStr(), request, new NullOutputStream());
		}
	}

	/**
	 * Connect and log in to the specified host
	 * @param ftp
	 * @throws SocketException
	 * @throws IOException
	 */
	private void connectAndLogin(FTPClient ftp) throws SocketException, IOException {
		// Connect
		ftp.connect(host, port);

		// After a connection attempt, check the return code
		int reply = ftp.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			log.error("FTP server " + hostStr() + " refused connection.");
			disconnect(ftp);
		}
		
		if (!ftp.login(username, password)) {
			log.error("Cannot login to FTP server " + hostStr() + " with user " + username);
			disconnect(ftp);
		}
	}

	/**
	 * Disconnect this {@link FTPClient} if connected
	 * @param ftp
	 */
	private void disconnect(FTPClient ftp) {
		if (ftp.isConnected()) {
			try {
				ftp.disconnect();
			} catch (IOException ex) {
				log.error("Error on FTP disconnect from " + hostStr(), ex);
			}
		}
	}
	
	/**
	 * @return host + ":" + port
	 */
	private String hostStr() {
		return host + ":" + port;
	}
}
