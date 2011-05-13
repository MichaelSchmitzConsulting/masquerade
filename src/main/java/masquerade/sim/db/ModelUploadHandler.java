package masquerade.sim.db;

import java.io.File;

import masquerade.sim.app.UploadHandler.UploadedContentHandler;

/**
 * Imports uploaded simulation configuration into the model repository
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
			ModelImport importer = new PersistentModelImport(modelRepository);
			importer.importModel(file);
		} finally {
			modelRepository.endSession();
		}
	}
}
