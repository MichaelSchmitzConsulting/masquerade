package masquerade.sim.util;

import org.osgi.framework.Bundle;

/**
 * Loads classes via {@link Bundle#loadClass(String)} in an OSGi context.
 */
public class BundleClassLoader extends ClassLoader {
	private final Bundle bundle;

	public BundleClassLoader(Bundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> type = bundle.loadClass(name);
		return type != null ? type : super.loadClass(name);
	}
}
