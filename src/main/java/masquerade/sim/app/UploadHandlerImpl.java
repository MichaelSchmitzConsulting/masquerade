package masquerade.sim.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import com.vaadin.ui.Upload;

/**
 * Handles file uploads to a temporary file. One upload per handler can be in progress
 * at a time.
 */
public class UploadHandlerImpl implements UploadHandler {

	private final static StatusLog log = StatusLogger.get(UploadHandlerImpl.class);

	private UploadedContentHandler contentHandler;
	private long maxSize;

	private OutputStream outputStream;
	private File tempFile;
	private Upload upload;

	/**
	 * @param maxSize
	 */
	public UploadHandlerImpl(UploadedContentHandler contentHandler, long maxSize) {
		this.contentHandler = contentHandler;
		this.maxSize = maxSize;
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		if (outputStream != null) {
			log.error("Upload failed - upload in progress");
			return null;
		}
		
		try {
			tempFile = File.createTempFile("masquerade-", "-upload");
			outputStream  = new FileOutputStream(tempFile);
			return outputStream;
		} catch (IOException e) {
			log.error("Upload failed", e);
			return null;
		}
	}

	@Override
	public void uploadStarted(Upload upload) {
		this.upload = upload;
	}

	@Override
	public void uploadDone() {
		try {
			contentHandler.onContentUploaded(tempFile);
		} catch (IOException e) {
			log.error("Processing uploaded content failed", e);
		}
		finishUpload();
	}

	@Override
	public void uploadFailed() {
		finishUpload();
	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
		if (upload != null && (readBytes > maxSize || contentLength > maxSize)) {
			upload.interruptUpload();
		}
	}

	private void finishUpload() {
		OutputStream outputStream = this.outputStream;
		File tempFile = this.tempFile;
		this.outputStream = null;
		this.tempFile = null;
		
		// Close outputstream for temp file
		try {
			outputStream.close();
		} catch (IOException e) {
			log.error("Cannot close upload output stream", e);
		}
		
		// Delete temp file
		tempFile.delete();
	}
}
