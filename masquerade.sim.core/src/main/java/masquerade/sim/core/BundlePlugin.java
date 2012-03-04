package masquerade.sim.core;

import static org.osgi.framework.Bundle.ACTIVE;

import java.io.File;
import java.lang.ref.WeakReference;

import masquerade.sim.plugin.Plugin;
import masquerade.sim.plugin.PluginException;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

/**
 * A {@link Plugin} which is installed as OSGi bundle
 */
public class BundlePlugin implements Plugin {

	private final WeakReference<Bundle> bundle;
	private final File jar;
	private final InternalPluginManager pluginManager;
	
	public BundlePlugin(Bundle bundle, File jar, InternalPluginManager pluginManager) {
		this.bundle = new WeakReference<Bundle>(bundle);
		this.jar = jar;
		this.pluginManager = pluginManager;
	}

	public Bundle getBundle() {
		return bundle.get();
	}
	
	public File getJar() {
		return jar;
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return bundle.get().getSymbolicName();
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#getDescription()
	 */
	@Override
	public String getDescription() {
		String description = (String) bundle.get().getHeaders().get(Constants.BUNDLE_NAME);
		return description == null ? "" : description;
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#getVersion()
	 */
	@Override
	public String getVersion() {
		return bundle.get().getVersion().toString();
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#getState()
	 */
	@Override
	public State getState() {
		int state = bundle.get().getState();
		switch (state) {			
		case ACTIVE:
			return State.STARTED;
		default:
			return State.STOPPED;
		}
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#start()
	 */
	@Override
	public void start() throws PluginException {
		pluginManager.startPlugin(this);
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#stop()
	 */
	@Override
	public void stop() throws PluginException {
		pluginManager.stopPlugin(this);
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.Plugin#remove()
	 */
	@Override
	public void remove() throws PluginException {
		if (getState() == State.STARTED) {
			pluginManager.stopPlugin(this);
		}
		
		pluginManager.removePlugin(this);
	}

	@Override
	public String toString() {
		return "BundlePlugin [getIdentifier()=" + getIdentifier()
				+ ", getVersion()=" + getVersion() + ", getState()="
				+ getState() + ", getDescription()=" + getDescription() + "]";
	}
}
