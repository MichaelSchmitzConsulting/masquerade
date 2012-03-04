package masquerade.sim.plugin;

import java.util.Collection;

public interface PluginRegistry {

	<T> void registerExtension(Class<T> extensionBase, Class<? extends T> extension);

	void registerPropertyEditor(Class<?> beanType, String propertyName, FieldFactory fieldFactory);
	
	Collection<Class<?>> getExtensions(Class<?> base);
	
	FieldFactory getPropertyEditor(Class<?> beanType, String propertyName);
	
	ClassLoader createExtensionClassLoader();

	/**
	 * @return Extension class with this name if found, <code>null</code> otherwise
	 */
	Class<?> getExtensionClassByName(String name);
}
