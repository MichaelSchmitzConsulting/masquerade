package masquerade.sim.app.ui2.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;
import masquerade.sim.util.WindowUtil;

import org.apache.commons.io.IOUtils;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;

/**
 * Handles simulation model uploads
 */
@SuppressWarnings("serial")
public class ImportUploadHandler implements UploadHandler {
	public static interface UploadListener {
		public void onUploadDone(InputStream stream);
	}
	
	private final static StatusLog log = StatusLogger.get(ImportUploadHandler.class);

	private final Window window;
	private final long maxSize;
	private final UploadListener uploadListener;

	private OutputStream outputStream;
	private File tempFile;
	private Upload upload;

	public ImportUploadHandler(UploadListener uploadListener, long maxSize, Window window) {
		this.uploadListener = uploadListener;
		this.maxSize = maxSize;
		this.window = window;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		if (outputStream != null) {
			log.error("Upload failed - upload in progress");
			return null;
		}

		try {
			tempFile = File.createTempFile("masquerade-", "-upload");
			outputStream = new FileOutputStream(tempFile);
			return outputStream;
		} catch (IOException e) {
			log.error("Upload failed", e);
			return null;
		}
	}

	@Override
	public void uploadStarted(StartedEvent event) {
		this.upload = event.getUpload();
	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
		if (upload != null && (readBytes > maxSize || contentLength > maxSize)) {
			upload.interruptUpload();
		}
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(tempFile);
			uploadListener.onUploadDone(stream);
		} catch (Exception e) {
			log.error("Processing uploaded content failed", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		cleanupTempFile();
	}

	private void cleanupTempFile() {
		OutputStream outputStream = this.outputStream;
		File tempFile = this.tempFile;
		this.outputStream = null;
		this.tempFile = null;

		// Close outputstream for temp file
		try {
			if (outputStream != null) {
				outputStream.close();
			}
		} catch (IOException e) {
			log.error("Cannot close upload output stream", e);
		}

		// Delete temp file
		tempFile.delete();
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		cleanupTempFile();
		
		Exception reason = event.getReason();
		log.error("Upload failed", reason);
		String reasonMsg = StringUtil.strackTrace(reason);
		WindowUtil.showErrorNotification(window, "Upload failed", "Upload failed:\n" + reasonMsg == null ? "Unknown reason (file too large?)" : reasonMsg);
	}
}
