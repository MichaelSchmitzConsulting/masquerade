package masquerade.sim.db;

import java.io.File;

import masquerade.sim.app.UploadHandler.UploadedContentHandler;

/**
 * Imports uploaded simulation configuration into 
 * the {@link ModelRepository}.
 */
public class ModelUploadHandler implements UploadedContentHandler {

	private ModelRepositoryFactory modelRepositoryFactory;

	public ModelUploadHandler(ModelRepositoryFactory modelRepositoryFactory) {
		this.modelRepositoryFactory = modelRepositoryFactory;
	}

	@Override
	public void onContentUploaded(File file) {
		ModelRepository modelRepository = modelRepositoryFactory.startModelRepositorySession();
		try {
			// Clear existing configuration
			// TODO: Remove clear(), implement sensible merging algorithm (mostly for
			// channels with same name, probably reference channels by ID instead of
			// requiring name to be unique).
			modelRepository.clear();
			
			// Import new configuration
			ModelImport importer = new ModelRepositoryImport(modelRepository);
			importer.importModel(file);
		} finally {
			modelRepository.endSession();
		}
	}
}
