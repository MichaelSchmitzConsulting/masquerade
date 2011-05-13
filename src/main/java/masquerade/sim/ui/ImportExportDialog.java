package masquerade.sim.ui;

import masquerade.sim.app.UploadHandler;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;
import masquerade.sim.util.WindowUtil;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Simulation configuration import/export dialog.
 */
public class ImportExportDialog extends Window implements Upload.FailedListener, Upload.SucceededListener {

	private static final StatusLog log = StatusLogger.get(ImportExportDialog.class);
	
	public static void showModal(Window parent, UploadHandler uploadHandler, DownloadHandler downloadHandler) {
		ImportExportDialog dialog = new ImportExportDialog("Import/Export Simulation Configuration", uploadHandler, downloadHandler);
		WindowUtil.getRoot(parent).addWindow(dialog);
	}
	
	private UploadHandler uploadHandler;
	private DownloadHandler downloadHandler;
	
	public ImportExportDialog(String caption, UploadHandler uploadHandler, DownloadHandler downloadHandler) {
		super(caption);
		
		this.uploadHandler = uploadHandler;
		this.downloadHandler = downloadHandler;
		
		setModal(true);
		setWidth("500px");
		setResizable(false);
		setContent(buildLayout());
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		uploadHandler.uploadDone();	
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		Exception reason = event.getReason();
		log.error("Upload failed", reason);
		String reasonMsg = StringUtil.strackTrace(reason);
		WindowUtil.showErrorNotification(
				getWindow(),
				"Upload failed", "Upload failed:\n" + reasonMsg == null ? "Unknown reason (file too large?)" : reasonMsg);
		uploadHandler.uploadFailed();
	}

	private ComponentContainer buildLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		
		// Label
		layout.addComponent(new Label("Export current simulation configuration"));
		
		// Download button
		layout.addComponent(new Button("Download", new DownloadClickListener(getWindow(), downloadHandler)));
		
		// Label
		layout.addComponent(new Label("Add simulation configuration"));
		
		// Upload field
		Upload upload = new Upload("Upload", uploadHandler);
		upload.addListener((Upload.FailedListener) this);
		upload.addListener((Upload.SucceededListener) this);
		upload.addListener((Upload.ProgressListener) uploadHandler);
		layout.addComponent(upload);
		
		return layout;
	}
}
