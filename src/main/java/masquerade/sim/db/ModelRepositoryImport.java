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
	public void importModel(File file) {
		DatabaseLifecycle lifecycle = new DatabaseLifecycle();
		
		try {
			ObjectContainer db = lifecycle.start(file);
			ObjectSet<Object> resultSet = db.query(Object.class);
			importObjects(resultSet);
		} finally {
			lifecycle.stop();
		}
	}
	
	private void importObjects(Collection<Object> resultSet) {
		for (Object obj : resultSet) {
			modelRepository.notifyCreate(obj);
		}
	}
}
