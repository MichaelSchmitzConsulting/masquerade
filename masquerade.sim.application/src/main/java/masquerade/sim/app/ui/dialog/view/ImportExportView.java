package masquerade.sim.app.ui2.dialog.view;

import java.io.IOException;

import masquerade.sim.app.ui2.dialog.UploadHandler;

import com.vaadin.terminal.Resource;

/**
 * Dialog for importing/exporting simulation settings
 */
public interface ImportExportView {
		
	interface ImportExportViewCallback {
		Resource provideDownload() throws IOException;
		void setReplaceExistingConfiguration(boolean value);
		UploadHandler createUploadHandler();
	}

	void bind(ImportExportViewCallback callback);
	void show();
	void showUploadFailedErrorMessage(String reasonMsg);
}
