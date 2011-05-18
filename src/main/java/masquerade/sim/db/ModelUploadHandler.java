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
			// Clear existing configuration if replace is chosen by the user
			// TODO: Improve import by loading the uploaded database into a separate
			// repository (with no channel actiations), and if that succeeds, clear
			// the existing repo and replace its contents with the uploaded objects.
			if (isReplaceExisting) {
				log.info("Clearing existing simulation configuration before import with activated replace");
				modelRepository.clear();
			}
			
			// Import new configuration
			log.info("Importing uploaded configuration...");
			ModelImport importer = new ModelRepositoryImport(modelRepository);
			importer.importModel(file);
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
