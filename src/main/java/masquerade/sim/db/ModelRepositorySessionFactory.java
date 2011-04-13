package masquerade.sim.db;

import com.db4o.ObjectContainer;

public class ModelRepositorySessionFactory implements ModelRepositoryFactory {

	private ObjectContainer db;

	public ModelRepositorySessionFactory(ObjectContainer db) {
		this.db = db;
	}

	@Override
	public ModelRepository startModelRepositorySession() {
		return new ModelRepository(db); // todo: use session
	}

}
