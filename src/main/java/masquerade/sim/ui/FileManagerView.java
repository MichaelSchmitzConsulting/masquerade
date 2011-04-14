package masquerade.sim.ui;

import java.io.File;

import masquerade.sim.DeleteListener;
import masquerade.sim.model.FileType;
import masquerade.sim.ui.MasterDetailView.AddListener;
import masquerade.sim.ui.UploadFileWindow.UploadResultListener;

import com.vaadin.data.Container;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.ui.HorizontalLayout;

/**
 * A view for managing simulation artifacts such as templates.
 * @see FileType
 */
public class FileManagerView extends HorizontalLayout {
	private static final String[] VISIBLE_FILE_COLS = new String[] {
		FilesystemContainer.PROPERTY_NAME, FilesystemContainer.PROPERTY_SIZE, FilesystemContainer.PROPERTY_LASTMODIFIED };

	public FileManagerView(File artifactRoot) {
		HorizontalLayout layout = this;
		layout.setMargin(true);
		layout.setSizeFull();

		// Create file list view
		final MasterDetailView view = new MasterDetailView();
		layout.addComponent(view);

		// Add filesystem container to display templates
		final File templateRoot = new File(artifactRoot, FileType.TEMPLATE.name().toLowerCase());
		if (templateRoot.exists()) {
			updateArtifactView(view, templateRoot);
		}

		view.addDeleteListener(new DeleteListener() {
			@Override
			public void notifyDelete(Object obj) {
				File file = (File) obj;
				file.delete();
				getWindow().showNotification("File " + file.getName() + " deleted");
				updateArtifactView(view, templateRoot);
			}
		});
		final UploadResultListener uploadListener = new UploadResultListener() {
			@Override
			public void onUploadFailed() {
				// Ignored, upload window already shows error notification
			}

			@Override
			public void onUploadDone(File file) {
				updateArtifactView(view, templateRoot);
			}
		};

		view.addAddListener(new AddListener() {
			@Override
			public void onAdd() {
				UploadFileWindow.showModal(getWindow(), "Upload Artifact", templateRoot, uploadListener);
			}
		});
	}

	private void updateArtifactView(MasterDetailView view, final File templateRoot) {
		Container container = new FilesystemContainer(templateRoot);
		view.setDataSource(container, VISIBLE_FILE_COLS);
	}

}
