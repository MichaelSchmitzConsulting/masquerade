package masquerade.sim.ui;

import masquerade.sim.app.UploadHandler;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.StringUtil;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
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
	private static final boolean IS_REPLACE_DEFAULT = false;

	/**
	 * Listener for dialog clients to received configuration options
	 * set by the user in the dialog.
	 */
	public interface ImportExportConfigListener {
		/**
		 * Flag signallying whether the existing simulation configuration should be 
		 * replaced by the uploaded configuration, or if the two should be merged.
		 * @param isReplaceExisting 
		 */
		void setReplaceExistingConfiguration(boolean isReplaceExisting);
	}
	
	public static void showModal(Window parent, UploadHandler uploadHandler, DownloadHandler downloadHandler, ImportExportConfigListener configListener) {
		ImportExportDialog dialog = new ImportExportDialog("Import/Export Simulation Configuration", uploadHandler, downloadHandler, configListener);
		WindowUtil.getRoot(parent).addWindow(dialog);
	}
	
	private UploadHandler uploadHandler;
	private DownloadHandler downloadHandler;
	private CheckBox replaceCheckbox;
	private ImportExportConfigListener configListener;
	
	public ImportExportDialog(String caption, UploadHandler uploadHandler, DownloadHandler downloadHandler, ImportExportConfigListener configListener) {
		super(caption);
		
		this.uploadHandler = uploadHandler;
		this.downloadHandler = downloadHandler;
		this.configListener = configListener;
		
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
		layout.addComponent(new Label("<b>Export current simulation configuration</b>", Label.CONTENT_XHTML));
		
		// Download button
		layout.addComponent(new Button("Download", new DownloadClickListener(getWindow(), downloadHandler)));
		
		// Label
		layout.addComponent(new Label("<b>Upload simulation configuration</b>", Label.CONTENT_XHTML));
		
		// Upload field
		Upload upload = new Upload(null, uploadHandler);
		upload.addListener((Upload.FailedListener) this);
		upload.addListener((Upload.SucceededListener) this);
		upload.addListener((Upload.ProgressListener) uploadHandler);
		layout.addComponent(upload);
		
		// Replace existing checkbox
		replaceCheckbox = new CheckBox(
				"Replace existing configuration");
		replaceCheckbox.setValue(IS_REPLACE_DEFAULT);
		configListener.setReplaceExistingConfiguration(IS_REPLACE_DEFAULT);
		replaceCheckbox.setImmediate(true);
		replaceCheckbox.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean value = (Boolean) event.getProperty().getValue();
				configListener.setReplaceExistingConfiguration(value);
			}
		});
		layout.addComponent(replaceCheckbox);
		
		// Upload comment label
		layout.addComponent(new Label("Channels with conflicting names will be replaced in any case"));
		
		return layout;
	}
}
