package masquerade.sim.db;

import com.db4o.ObjectContainer;

/**
 * Factory for {@link ModelRepository} session instances
 */
public class ModelRepositorySessionFactory implements ModelRepositoryFactory {

	private ObjectContainer db;

	public ModelRepositorySessionFactory(ObjectContainer db) {
		this.db = db;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelRepository startModelRepositorySession() {
		ObjectContainer session = db.ext().openSession();
		return new ModelRepository(session);
	}

}
