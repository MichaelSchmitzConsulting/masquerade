package masquerade.sim.db;

/**
 * Factory for {@link ModelRepository} session instances
 */
public interface ModelRepositoryFactory {
	
	/**
	 * Returns a {@link ModelRepository} instance. Clients must
	 * call {@link ModelRepository#endSession()} when done.
	 */
	ModelRepository startModelRepositorySession();

}
