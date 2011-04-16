package masquerade.sim.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.model.NamespacePrefix;
import masquerade.sim.model.NamespaceResolver;

public class ModelNamespaceResolver implements NamespaceResolver {

	private ModelRepositoryFactory modelRepositoryFactory;
	
	/**
	 * @param modelRepositoryFactory
	 */
	public ModelNamespaceResolver(ModelRepositoryFactory modelRepositoryFactory) {
		this.modelRepositoryFactory = modelRepositoryFactory;
	}

	/**
	 * @return Namespace for this prefix, or <code>null</code> if not found
	 */
	@Override
	public String resolveNamespacePrefix(String prefix) {
		if (prefix == null) {
			return null;
		}
		
		Collection<NamespacePrefix> all = getAllNamespaces();

		for (NamespacePrefix ns : all) {
			if (prefix.equals(ns.getPrefix())) {
				return ns.getNamespace();
			}
		}
		return null;
	}

	@Override
	public Map<String, String> getKnownNamespaces() {		
		Collection<NamespacePrefix> all = getAllNamespaces();

		Map<String, String> map = new HashMap<String, String>();
		for (NamespacePrefix ns : all) {
			map.put(ns.getPrefix(), ns.getNamespace());
		}
		return map;
	}

	private Collection<NamespacePrefix> getAllNamespaces() {
		ModelRepository repo = modelRepositoryFactory.startModelRepositorySession();
		Collection<NamespacePrefix> all;
		try {
			all = repo.getAll(NamespacePrefix.class);
		} finally {
			repo.endSession();
		}
		return all;
	}

}
