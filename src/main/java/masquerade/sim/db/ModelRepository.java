package masquerade.sim.db;

import java.util.Collection;

import masquerade.sim.CreateApprover;
import masquerade.sim.CreateListener;
import masquerade.sim.DeleteApprover;
import masquerade.sim.DeleteListener;
import masquerade.sim.UpdateListener;
import masquerade.sim.model.Channel;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Settings;

/**
 * Repository for simulation configuration domain model 
 * objects
 */
public interface ModelRepository extends UpdateListener, DeleteListener, CreateListener, CreateApprover, DeleteApprover {

	/**
	 * End the session for this repository. Clients must
	 * not use the repository after endSession() any more.
	 */
	void endSession();

	/**
	 * @return All {@link Channel channels} contained in this repository
	 */
	Collection<Channel> getChannels();
	
	/**
	 * @param name Channel name
	 * @return Channel with this name, or <code>null</code> if not found
	 */
	Channel getChannelByName(final String name);
	
	/**
	 * @return All {@link RequestMapping request mappings} contained in this repository
	 */
	Collection<RequestMapping<?>> getRequestMappings();
	
	/**
	 * @return All {@link RequestIdProvider request ID providers} contained in this repository
	 */
	Collection<RequestIdProvider<?>> getRequestIdProviders();
	
	/**
	 * @return All {@link Script scripts} contained in this repository
	 */
	Collection<Script> getScripts();
	
	/**
	 * @return The {@link Settings} instance contained in this repository
	 */
	Settings getSettings();

	/**
	 * @param baseType Return type or supertype
	 * @param usedName Name of the object
	 * @return A {@link Collection} of all objects with the given type or supertype with a field "name" 
	 */
	<T> Collection<? extends T> getByName(Class<? extends T> baseType, String usedName);
	
	/**
	 * @param <T> Type of objects to query the repository for
	 * @param type Type class 
	 * @return {@link Collection} of all objects with the given type or supertype
	 */
	<T> Collection<T> getAll(Class<T> type);

	/**
	 * Query the repository for known implementations of
	 * domain model types.
	 * @param modelBaseType Model class/interface to be implemented by returned types
	 * @return All registered types implementing modelBaseType
	 */
	Collection<Class<?>> getModelImplementations(Class<?> modelBaseType);

	/**
	 * Clear all domain objects from this repository
	 */
	void clear();
}