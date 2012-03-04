package masquerade.sim.model.script;

import java.io.IOException;

import javax.script.ScriptEngineManager;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * Provides a JSR 223 {@link ScriptEngineManager} with an OSGi-aware classloader 
 * to be able to load script engines installed as OSGi bundles.
 */
@Component
public class ScriptEngineProvider {
	private static ScriptEngineManager manager;
	private static BundleContext bundleContext;
	
	@Activate
	protected void activate(ComponentContext componentContext) throws Exception {
		bundleContext = componentContext.getBundleContext();
	}
	
	public synchronized static ScriptEngineManager getScriptEngineManager() {
		if (manager == null) {
			manager = createManager();
		}
		
		return manager;
	}

	private static ScriptEngineManager createManager() {
		try {
			return new OSGiScriptEngineManager(bundleContext);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to create OSGiScriptEngineManager from bundle context for bundle " + 
					bundleContext.getBundle().getSymbolicName(), e);
		}
	}
}
