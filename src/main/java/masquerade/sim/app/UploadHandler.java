package masquerade.sim.app;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;

/**
 * Handler for a single file upload. Is a {@link Receiver} providing an {@link OutputStream}, and
 * handles upload sucess/failure. The user will have been presented with information about upload
 * success/failure, the UploadHandler is responsible for streaming content and handling it only.
 */
public interface UploadHandler extends Receiver, ProgressListener {
	interface UploadedContentHandler {
		void onContentUploaded(File tempFile) throws IOException;
	}
	
	void uploadStarted(Upload upload);
	void uploadDone();
	void uploadFailed();
}
