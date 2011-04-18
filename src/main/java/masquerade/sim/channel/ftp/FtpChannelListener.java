package masquerade.sim.channel.ftp;

import java.util.logging.Level;
import java.util.logging.Logger;

import masquerade.sim.model.ChannelListener;
import masquerade.sim.model.impl.AbstractChannelListener;
import masquerade.sim.model.impl.FtpChannel;

/**
 * A {@link ChannelListener} receiving requests in files over FTP
 */
public class FtpChannelListener extends AbstractChannelListener<FtpChannel> {

	private static final Logger log = Logger.getLogger(FtpChannelListener.class.getName());
	private static final long MAX_WAIT = 20000;
	
	private FtpReceiverThread receiverThread;
	
	/**
	 * Starts an {@link FtpReceiverThread}
	 */
	@Override
	protected synchronized void onStart(FtpChannel channel) {
		onStop();
		
		receiverThread = new FtpReceiverThread(this,
			channel.getHost(), channel.getPort(), channel.getPollingPeriodMs(), channel.getRemoteDirectory(), channel.getRemoteFileName(),
			channel.getUsername(), channel.getPassword(), channel.isDeleteRemoteFileAfterRetrieval());
	}

	/**
	 * Stops the {@link FtpReceiverThread}
	 */
	@Override
	protected synchronized void onStop() {
		receiverThread.interrupt();
		try {
			receiverThread.join(MAX_WAIT);
		} catch (InterruptedException e) {
			log.log(Level.SEVERE, "Interrupted while stopping FTP receiver thread", e);
		}
		receiverThread = null;
	}
}
