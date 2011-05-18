package masquerade.sim.db;

import java.io.File;
import java.util.Collection;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

/**
 * {@link ModelImport} implementation importing simulation
 * configuration into the {@link ModelRepository}.
 */
public class ModelRepositoryImport implements ModelImport {

	private ModelRepository modelRepository;

	public ModelRepositoryImport(ModelRepository modelRepository) {
		this.modelRepository = modelRepository;
	}

	@Override
	public void importModel(File file, boolean isReplaceExisting) {
		DatabaseLifecycle lifecycle = new DatabaseLifecycle();
		
		try {
			// Load uploaded configuration
			ObjectContainer db = lifecycle.start(file);
			ObjectSet<Object> resultSet = db.query(Object.class);
			
			// Successfully loaded - now import all objects in the new
			// configuration into the model repository.
			importObjects(resultSet, isReplaceExisting);
		} finally {
			lifecycle.stop();
		}
	}
	
	private void importObjects(Collection<Object> resultSet, boolean isReplaceExisting) {
		// Should the existing configuration be replaced?
		if (isReplaceExisting) {
			modelRepository.clear();
		}
		
		for (Object obj : resultSet) {
			modelRepository.notifyCreate(obj);
		}
	}
}
