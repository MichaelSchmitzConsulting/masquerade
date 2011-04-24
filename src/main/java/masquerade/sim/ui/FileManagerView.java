package masquerade.sim.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import masquerade.sim.DeleteListener;
import masquerade.sim.model.FileType;
import masquerade.sim.ui.MasterDetailView.AddListener;
import masquerade.sim.ui.UploadFileWindow.UploadResultListener;
import masquerade.sim.util.StringUtil;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * A view for managing simulation artifacts such as templates.
 * @see FileType
 */
public class FileManagerView extends VerticalLayout {
	private static final String[] VISIBLE_FILE_COLS = new String[] {
		FilesystemContainer.PROPERTY_NAME, FilesystemContainer.PROPERTY_SIZE, FilesystemContainer.PROPERTY_LASTMODIFIED };
	
	private static final Logger log = Logger.getLogger(FileManagerView.class.getName());
	
	private final File artifactRoot;
	private Select fileTypeSelect;

	public FileManagerView(final File artifactRoot) {
		this.artifactRoot = artifactRoot;
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		// File type select
		fileTypeSelect = new Select();
		fileTypeSelect.setImmediate(true);
		fileTypeSelect.setInvalidAllowed(false);
		fileTypeSelect.setNullSelectionAllowed(false);
		fileTypeSelect.setNewItemsAllowed(false);
		List<String> types = typeSelection(FileType.values());
		BeanItemContainer<String> fileTypes = new BeanItemContainer<String>(String.class, types);
		fileTypeSelect.setContainerDataSource(fileTypes);
		fileTypeSelect.select(types.get(0));
		addComponent(fileTypeSelect);
		
		// Create file list view
		final MasterDetailView view = new MasterDetailView();
		addComponent(view);
		setExpandRatio(view, 1.0f);

		// Add filesystem container to display templates
		updateArtifactView(view, getSelectedSubdir());

		addListeners(artifactRoot, view);
	}

	private void addListeners(final File artifactRoot, final MasterDetailView view) {
		// Add remove button listener to delete files
		view.addDeleteListener(new DeleteListener() {
			@Override public void notifyDelete(Object obj) {
				File file = (File) obj;
				file.delete();
				getWindow().showNotification("File " + file.getName() + " deleted");
				updateArtifactView(view, getSelectedSubdir());
			}
		});
		
		// Create listener handling upload results
		final UploadResultListener uploadListener = new UploadResultListener() {
			@Override public void onUploadFailed() {
				// Ignored, upload window already shows error notification
			}

			@Override public void onUploadDone(File file) {
				updateArtifactView(view, getSelectedSubdir());
			}
		};

		// Add listener to the Add button for uploading files
		view.addAddListener(new AddListener() {
			@Override public void onAdd() {
				File uploadTargetDir = new File(artifactRoot, getSelectedSubdir());
				UploadFileWindow.showModal(getWindow(), "Upload Artifact", uploadTargetDir, uploadListener);
			}
		});
		
		// Add listener to the file type select to update the list of files
		fileTypeSelect.addListener(new ValueChangeListener() {
			@Override public void valueChange(ValueChangeEvent event) {
				String subdir = (String) event.getProperty().getValue();
				updateArtifactView(view, subdir);
			}
		});
	}
	
	private String getSelectedSubdir() {
		return ((String) fileTypeSelect.getValue()).toLowerCase();
	}

	/**
	 * Reload artifact list
	 * @param view
	 * @param subdir
	 */
	private void updateArtifactView(MasterDetailView view, final String subdir) {
		File root = new File(artifactRoot, subdir);
		if (root.exists() && root.canRead() && root.isDirectory()) {
			Container container = new FilesystemContainer(root);
			view.setDataSource(container);
			view.setVisibleColumns(VISIBLE_FILE_COLS);
		} else {
			log.warning("Artifact directory " + root.getAbsolutePath() + "does not exist or is not readable");
		}
	}

	private static List<String> typeSelection(FileType[] values) {
		List<String> ret = new ArrayList<String>();
		for (FileType type : values) {
			String name = StringUtil.fromCamelCase(type.name().toLowerCase());
			ret.add(name);
		}
		return ret;
	}
}
