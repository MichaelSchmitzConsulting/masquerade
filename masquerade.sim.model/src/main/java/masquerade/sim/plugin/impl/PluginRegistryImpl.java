package masquerade.sim.plugin.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import masquerade.sim.plugin.FieldFactory;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * Registry where Masquerade plugins register their model interface
 * implementations, and custom property editor UI components for 
 * non-standard property types (for example, a list of choices
 * or a special String format such as an IP address).
 */
@Component
@Service
public class PluginRegistryImpl implements PluginRegistry {

	private final static StatusLog statusLog = StatusLogger.get(PluginRegistryImpl.class);
	
	private Map<Class<?>, Collection<Class<?>>> modelImpls = new HashMap<Class<?>, Collection<Class<?>>>();
	private Map<String, Map<Class<?>, FieldFactory>> propertyEditors = new HashMap<String, Map<Class<?>, FieldFactory>>();
	private Map<String, Class<?>> fqnToImplementationCache = new HashMap<String, Class<?>>();
	
	@Override
	public <T> void registerExtension(Class<T> extensionBase, Class<? extends T> extension) {
		synchronized (modelImpls) {
			Collection<Class<?>> impls = modelImpls.get(extensionBase);
			if (impls == null) {
				impls = new LinkedHashSet<Class<?>>();
				modelImpls.put(extensionBase, impls);
			}
			impls.add(extension);
			fqnToImplementationCache.put(extension.getName(), extension);
		}
		
		statusLog.trace("Extension for " + extensionBase.getName() + " registered: " + extension.getName());
	}

	@Override
	public void registerPropertyEditor(Class<?> beanType, String propertyName, FieldFactory fieldFactory) {
		synchronized (propertyEditors) {
			Map<Class<?>, FieldFactory> map = propertyEditors.get(propertyName);
			if (map == null) {
				map = new LinkedHashMap<Class<?>, FieldFactory>();
				propertyEditors.put(propertyName, map);
			}
			
			map.put(beanType, fieldFactory);
		}
		statusLog.trace("Property editor registered: " + beanType.getName() + "." + propertyName);
	}
	
	@Override
	public Collection<Class<?>> getExtensions(Class<?> base) {
		synchronized (modelImpls) {
			Collection<Class<?>> types = modelImpls.get(base);
			return types == null ? Collections.<Class<?>>emptySet() : new ArrayList<Class<?>>(types);
		}
	}

	@Override
	public FieldFactory getPropertyEditor(Class<?> beanType, String propertyName) {
		synchronized (propertyEditors) {
			Map<Class<?>, FieldFactory> map = propertyEditors.get(propertyName);
			if (map != null) {
				for (Map.Entry<Class<?>, FieldFactory> entry : map.entrySet()) {
					Class<?> type = entry.getKey();
					
					if (type.isAssignableFrom(beanType)) {
						return entry.getValue();
					}
				}
			}
		}
		return null;
	}

	@Override
	public ClassLoader createExtensionClassLoader() {
		return new ExtensionClassLoader(this, PluginRegistryImpl.class.getClassLoader());
	}

	@Override	
	public Class<?> getExtensionClassByName(String fqn) {
		Class<?> type;
		synchronized (modelImpls) {
			type = fqnToImplementationCache.get(fqn);
		}
		return type;
	}
}
