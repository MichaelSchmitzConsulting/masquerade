package masquerade.sim.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import masquerade.sim.util.WindowUtil;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UploadFileWindow extends Window implements Receiver, FailedListener, SucceededListener {
	private File file;
	private File uploadTargetDir;
	private UploadResultListener listener;
	
	public interface UploadResultListener {
		void onUploadFailed();
		void onUploadDone(File file);
	}
	
	public static void showModal(Window parent, String caption, File uploadTargetDir, UploadResultListener listener) {
		Window window = new UploadFileWindow(caption, uploadTargetDir, listener);
		WindowUtil.getRoot(parent).addWindow(window);
	}
	
	/**
	 * @param caption
	 * @param listener2 
	 * @param uploadTargetDir2 
	 */
	private UploadFileWindow(String caption, File uploadTargetDir, UploadResultListener listener) {
		super(caption);
		this.uploadTargetDir = uploadTargetDir;
		this.listener = listener;
		
		setModal(true);
		
		VerticalLayout layout = (VerticalLayout) getContent();
		Upload upload = new Upload("Upload the file here", this);
		layout.addComponent(layout);
		
		 // Listen for events regarding the success of upload.
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
	}
	
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
		FileOutputStream fos = null; // Output stream to write to
        file = new File(uploadTargetDir, filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
    		WindowUtil.showErrorNotification(this, "Upload failed", "File creation failed: " + e.getMessage());
    		listener.onUploadFailed();
            return null;
        }

        return fos; // Return the output stream to write to
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		Exception reason = event.getReason();
		String msg = reason != null ? reason.getMessage() : "Unknown";
		WindowUtil.showErrorNotification(this, "Upload failed", "Cause: " + msg);
		listener.onUploadFailed();
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		listener.onUploadDone(file);		
	}
}
