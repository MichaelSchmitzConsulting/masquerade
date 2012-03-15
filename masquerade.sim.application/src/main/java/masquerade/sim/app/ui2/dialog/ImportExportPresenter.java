package masquerade.sim.app.ui2.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import masquerade.sim.app.ui2.dialog.ImportUploadHandler.UploadListener;
import masquerade.sim.app.ui2.dialog.view.ImportExportView;
import masquerade.sim.app.ui2.dialog.view.ImportExportView.ImportExportViewCallback;
import masquerade.sim.app.util.NamedFileResource;
import masquerade.sim.model.importexport.Exporter;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.importexport.impl.XmlImporter;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.SimulationModel;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.IOUtils;

import com.vaadin.Application;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Window;

/**
 * Presenter for {@link ImportExportView}
 */
public class ImportExportPresenter implements ImportExportViewCallback {
	private static final StatusLog log = StatusLogger.get(ImportExportPresenter.class);

	private static final long MAX_UPLOAD_SIZE = 2 * 1000 * 1000; // 2MB

	private final ImportExportView view;
	private final Exporter exporter;
	private final ModelRepository modelRepository;
	private final Window window;
	private final Application application;
	private final Importer importer;

	private boolean isReplaceExistingConfiguration;

	public ImportExportPresenter(ImportExportView view, ModelRepository modelRepository, Exporter exporter, Application application, Window window) {
		this.view = view;
		this.modelRepository = modelRepository;
		this.exporter = exporter;
		this.application = application;
		this.window = window;
		this.importer = new XmlImporter();
	}

	public void showDialog() {
		view.show();
	}
	
	@Override
	public Resource provideDownload() throws IOException {
		File tempFile = File.createTempFile("masquerade-", "-download");
		FileOutputStream stream = new FileOutputStream(tempFile);
		try {
			exportToStream(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}

		// TODO: Limit/cleanup temp files
		tempFile.deleteOnExit();

		final String fileName = "masquerade-export." + timestamp() + ".simulation";
		final String mimeType = "application/x-masquerade-simulation";

		return new NamedFileResource(tempFile, application, mimeType, fileName);
	}

	private void exportToStream(FileOutputStream stream) throws IOException {
		SimulationModel simulationModel = modelRepository.getSimulationModel();
		exporter.exportModelObject(simulationModel, stream);
	}

	private String timestamp() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS").format(new Date());
	}

	@Override
	public void setReplaceExistingConfiguration(boolean flag) {
		isReplaceExistingConfiguration = flag;
	}

	@Override
	public UploadHandler createUploadHandler() {
		return new ImportUploadHandler(new UploadListener() {
			@Override
			public void onUploadDone(InputStream stream) {
				handleUpload(stream);
			}
		}, MAX_UPLOAD_SIZE, window);
	}

	private void handleUpload(InputStream stream) {
		// Import new configuration
		log.info("Importing uploaded configuration (replace = " + isReplaceExistingConfiguration + ")...");
		importer.importModel(stream, isReplaceExistingConfiguration);
		log.info("Import done");

	}
}
