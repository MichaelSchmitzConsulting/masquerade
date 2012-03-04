package masquerade.sim.plugin.impl;

import masquerade.sim.plugin.PluginRegistry;

/**
 * {@link ClassLoader} loading extension classes registered in the {@link PluginRegistry},
 * falling back to the model bundle's class loader if required.
 */
public class ExtensionClassLoader extends ClassLoader {

	private final PluginRegistry pluginRegistry;

	public ExtensionClassLoader(PluginRegistry pluginRegistry, ClassLoader parent) {
		super(parent);
		this.pluginRegistry = pluginRegistry;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> type = pluginRegistry.getExtensionClassByName(name);
		return type != null ? type : super.loadClass(name);
	}
}
