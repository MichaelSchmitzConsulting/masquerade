package masquerade.sim.app.ui2.presenter;

import java.io.File;

import masquerade.sim.app.ui2.view.FileManagerView;
import masquerade.sim.app.ui2.view.FileManagerView.FileManagerViewCallback;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

/**
 * Presenter for {@link FileManagerView}
 */
public class FileManagerPresenter implements FileManagerViewCallback {

	private static final StatusLog log = StatusLogger.get(FileManagerPresenter.class);
	
	private final File fileRoot;
	private final FileManagerView view;

	public FileManagerPresenter(File fileRoot, FileManagerView view) {
		this.fileRoot = fileRoot;
		this.view = view;
	}

	@Override
	public void onRefresh() {
		updateFileList(view.getSelectedSubdir());
	}

	@Override
	public void onSubdirChanged(String subdir) {
		updateFileList(subdir);
	}

	private void updateFileList(String subdir) {
		File root = new File(fileRoot, subdir);
		if (root.exists() && root.canRead() && root.isDirectory()) {
			view.setFileList(root);
		} else {
			log.warning("Artifact directory " + root.getAbsolutePath() + "does not exist or is not readable");
		}
	}

	@Override
	public void onDelete(File file) {
		file.delete();
		view.showNotification("File " + file.getName() + " deleted");
		updateFileList(view.getSelectedSubdir());
	}

	@Override
	public void onUpload() {
		File uploadTargetDir = new File(fileRoot, view.getSelectedSubdir());
		view.uploadFile(uploadTargetDir);
	}
}
