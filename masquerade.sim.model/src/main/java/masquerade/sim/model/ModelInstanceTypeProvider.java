package masquerade.sim.model;

import java.util.Collection;

import masquerade.sim.model.ui.InstanceTypeProvider;
import masquerade.sim.plugin.PluginRegistry;

/**
 * Provides access to all available implementations of simulation model 
 * objects base types/interfaces. 
 */
public class ModelInstanceTypeProvider implements InstanceTypeProvider {

	private final Class<?> interfaceType;
	private final PluginRegistry pluginRegistry;

	public ModelInstanceTypeProvider(Class<?> interfaceType, PluginRegistry pluginRegistry) {
		this.interfaceType = interfaceType;
		this.pluginRegistry = pluginRegistry;
	}

	@Override
	public Collection<Class<?>> getInstanceTypes() {
		return pluginRegistry.getExtensions(interfaceType);
	}
}
