package masquerade.sim.db;

import java.io.File;

import masquerade.sim.app.UploadHandler.UploadedContentHandler;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.ui.ImportExportDialog.ImportExportConfigListener;

/**
 * Imports uploaded simulation configuration into 
 * the {@link ModelRepository}.
 */
public class ModelUploadHandler implements UploadedContentHandler, ImportExportConfigListener {

	private static final StatusLog log = StatusLogger.get(ModelUploadHandler.class);
	
	private ModelRepositoryFactory modelRepositoryFactory;
	private boolean isReplaceExisting;

	public ModelUploadHandler(ModelRepositoryFactory modelRepositoryFactory) {
		this.modelRepositoryFactory = modelRepositoryFactory;
	}

	@Override
	public void onContentUploaded(File file) {
		ModelRepository modelRepository = modelRepositoryFactory.startModelRepositorySession();
		try {
			// Import new configuration
			log.info("Importing uploaded configuration...");
			ModelImport importer = new ModelRepositoryImport(modelRepository);
			importer.importModel(file, isReplaceExisting);
			log.info("Import done");
		} finally {
			modelRepository.endSession();
		}
	}

	@Override
	public void setReplaceExistingConfiguration(boolean isReplaceExisting) {
		this.isReplaceExisting = isReplaceExisting;
	}
}
