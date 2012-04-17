package masquerade.sim.app.ui2.view.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import masquerade.sim.app.ui.UploadFileWindow;
import masquerade.sim.app.ui.UploadFileWindow.UploadResultListener;
import masquerade.sim.app.ui2.view.FileManagerView;
import masquerade.sim.model.FileType;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.ui.MasterDetailView;
import masquerade.sim.model.ui.MasterDetailView.AddListener;
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
@SuppressWarnings("serial")
public class FileManagerViewImpl extends VerticalLayout implements FileManagerView {
	private static final String[] VISIBLE_FILE_COLS = new String[] {
		FilesystemContainer.PROPERTY_NAME, FilesystemContainer.PROPERTY_SIZE, FilesystemContainer.PROPERTY_LASTMODIFIED };
	
	private Select fileTypeSelect;
	private FileManagerViewCallback callback;

	private MasterDetailView fileListView;

	public FileManagerViewImpl() {
		setCaption("File Manager");
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
		
		fileListView = new MasterDetailView();
		addComponent(fileListView);
		setExpandRatio(fileListView, 1.0f);

		addListeners();
	}

	public void bind(FileManagerViewCallback callback) {
		this.callback = callback;
	}

	private void addListeners() {
		// Add remove button listener to delete files
		fileListView.addDeleteListener(new DeleteListener() {
			@Override public void notifyDelete(Object obj) {
				File file = (File) obj;
				callback.onDelete(file);
			}
		});
		
		// Add listener to the Add button for uploading files
		fileListView.addAddListener(new AddListener() {
			@Override public void onAdd() {
				callback.onUpload();
			}
		});
		
		// Add listener to the file type select to update the list of files
		fileTypeSelect.addListener(new ValueChangeListener() {
			@Override public void valueChange(ValueChangeEvent event) {
				String subdir = (String) event.getProperty().getValue();
				callback.onSubdirChanged(subdir);
			}
		});
	}
	
	@Override
	public String getSelectedSubdir() {
		return ((String) fileTypeSelect.getValue()).toLowerCase();
	}

	@Override
	public void setFileList(File root) {
		Container container = new FilesystemContainer(root);
		fileListView.setDataSource(container);
		fileListView.setVisibleColumns(VISIBLE_FILE_COLS);
	}

	private static List<String> typeSelection(FileType[] values) {
		List<String> ret = new ArrayList<String>();
		for (FileType type : values) {
			String name = StringUtil.fromCamelCase(type.name().toLowerCase());
			ret.add(name);
		}
		return ret;
	}

	@Override
	public void showNotification(String msg) {
		getWindow().showNotification(msg);
	}

	@Override
	public void uploadFile(File uploadTargetDir) {
		// Create listener handling upload results
		final UploadResultListener uploadListener = new UploadResultListener() {
			@Override public void onUploadFailed() {
				// Ignored, upload window already shows error notification
			}

			@Override public void onUploadDone(File file) {
				callback.onRefresh();
			}
		};

		UploadFileWindow.showModal(getWindow(), "Upload Artifact", uploadTargetDir, uploadListener);
	}
}
