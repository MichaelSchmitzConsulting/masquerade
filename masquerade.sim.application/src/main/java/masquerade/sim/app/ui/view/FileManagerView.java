package masquerade.sim.app.ui.view;

import java.io.File;

import masquerade.sim.app.ui.Refreshable;

/**
 * View for managing files (e.g. response templates)
 */
public interface FileManagerView {
	public interface FileManagerViewCallback extends Refreshable {
		void onSubdirChanged(String subdir);
		void onDelete(File file);
		void onUpload();
	}

	void setFileList(File root);
	void showNotification(String msg);
	String getSelectedSubdir();
	void uploadFile(File uploadTargetDir);
}
