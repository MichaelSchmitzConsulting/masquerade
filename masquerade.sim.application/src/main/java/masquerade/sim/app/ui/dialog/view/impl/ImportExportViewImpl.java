package masquerade.sim.app.ui2.dialog.view.impl;

import java.io.IOException;

import masquerade.sim.app.ui2.dialog.UploadHandler;
import masquerade.sim.app.ui2.dialog.view.ImportExportView;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Simulation configuration import/export dialog.
 * @see ImportExportView
 */
@SuppressWarnings("serial")
public class ImportExportViewImpl extends Window implements ImportExportView {

	private static final StatusLog log = StatusLogger.get(ImportExportViewImpl.class);
	
	private final Window parentWindow;
	private ImportExportViewCallback callback;

	public ImportExportViewImpl(Window parentWindow) {
		super("Import/Export Simulation Configuration");
		this.parentWindow = parentWindow;
		
		setModal(true);
		setWidth("500px");
		setResizable(false);
	}
	
	@Override
	public void bind(ImportExportViewCallback callback) {
		this.callback = callback;
		setContent(buildLayout());
	}
	
	@Override
	public void show() {
		WindowUtil.getRoot(parentWindow).addWindow(this);
	}

	private CheckBox replaceCheckbox;

	private ComponentContainer buildLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		// Label
		layout.addComponent(new Label("<b>Export current simulation configuration</b>", Label.CONTENT_XHTML));

		// Download button
		Button downloadButton = new Button("Download", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					getWindow().open(callback.provideDownload());
				} catch (IOException e) {
					log.error("Download error", e);
					WindowUtil.showErrorNotification(ImportExportViewImpl.this, "Error creating download", e.getClass().getName() + ": " + e.getMessage());
				}
			}
		});
		layout.addComponent(downloadButton);

		// Label
		layout.addComponent(new Label("<b>Upload simulation configuration</b>", Label.CONTENT_XHTML));

		// Upload field
		UploadHandler uploadHandler = callback.createUploadHandler();
		Upload upload = new Upload(null, uploadHandler);
		upload.addListener((Upload.FailedListener) uploadHandler);
		upload.addListener((Upload.SucceededListener) uploadHandler);
		upload.addListener((Upload.ProgressListener) uploadHandler);
		layout.addComponent(upload);

		// Replace existing checkbox
		replaceCheckbox = new CheckBox("Replace existing configuration");
		replaceCheckbox.setValue(true);
		callback.setReplaceExistingConfiguration(true);
		replaceCheckbox.setImmediate(true);
		replaceCheckbox.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean value = (Boolean) event.getProperty().getValue();
				callback.setReplaceExistingConfiguration(value);
			}
		});
		layout.addComponent(replaceCheckbox);

		// Upload comment label
		layout.addComponent(new Label("Channels and simulations with conflicting IDs will be replaced in any case"));

		return layout;
	}

	@Override
	public void showUploadFailedErrorMessage(String reasonMsg) {
		WindowUtil.showErrorNotification(parentWindow, "Upload failed", "Upload failed:\n" + reasonMsg == null ? "Unknown reason (file too large?)" : reasonMsg);
	}
}