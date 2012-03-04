package masquerade.sim.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import masquerade.sim.plugin.Plugin;
import masquerade.sim.plugin.PluginException;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Implementation of {@link PluginManager}, manages plugin bundle lifecycle and
 * plugin bundle files in the Masquerade plugin directory.
 * 
 * @see HomeResolver#getPluginLocation()
 */
public class PluginManagerImpl implements InternalPluginManager, FrameworkListener {

	private static final StatusLog log = StatusLogger.get(PluginManagerImpl.class);
	
	private final File pluginLocation;
	private final BundleContext bundleContext;
	private final PackageAdmin packageAdmin;
	private final PluginDescriptor pluginDescriptor;
	
	private final Collection<BundlePlugin> plugins = new CopyOnWriteArrayList<BundlePlugin>();
	private final Collection<File> scheduledForDeletion = new ArrayList<File>();
	
	public PluginManagerImpl(File pluginLocation, BundleContext bundleContext, PackageAdmin packageAdmin) {
		this.pluginLocation = pluginLocation;
		this.bundleContext = bundleContext;
		this.packageAdmin = packageAdmin;
		
		this.pluginDescriptor = new PluginDescriptorImpl(pluginLocation);

		// Install a listener that deletes plugin JARs after they have been released
		bundleContext.addFrameworkListener(this);
	}

	private void deleteScheduledFiles() {
		Collection<File> toDelete;
		synchronized (scheduledForDeletion) {
			toDelete = new ArrayList<File>(scheduledForDeletion);
			scheduledForDeletion.clear();
		}
		
		for (File jar : toDelete) {
			if (jar.delete()) {
				log.info("Plugin JAR at " + jar.getAbsolutePath() + " has been deleted");
			} else {
				log.error("Plugin was succesfully uninstalled, but was unable to delete plugin JAR at " + jar.getAbsolutePath());
			}
		}
	}

	/**
	 * Install all plugin bundles available in the plugin directory. Not part of the
	 * public interface, used by core at startup only.
	 */
	@Override
	public void initialize() {
		log.info("Plugin manager is starting");
		
		Collection<File> bundleJars = pluginDescriptor.listPlugins();
			
		for (File bundleJar : bundleJars) {
			try {
				startInstalledPlugin(bundleJar);
			} catch (PluginException e) {
				log.error("Unable to install bundle at " + bundleJar.getAbsolutePath(), e);
			}
			
		}
		
		log.info("Plugin manager startup complete");
	}
	
	@Override
	public void shutdown() {
		log.info("Plugin manager shutdown started");
		
		List<Bundle> allBundles = new ArrayList<Bundle>();
		
		synchronized (plugins) {
			for (BundlePlugin plugin : plugins) {
				Bundle bundle = plugin.getBundle();
				allBundles.add(bundle);
				String bundleName = bundle.getSymbolicName();
				try {
					bundle.stop();
					bundle.uninstall();
					
					log.info("Plugin " + bundleName + " stopped");
				} catch (BundleException e) {
					log.error("Unable to stop/uninstall plugin " + bundleName + " during PluginManager shutdown", e);
				}
			}
			
			plugins.clear();
		}
		
		log.info("Plugin manager shutdown complete, requesting package refresh");
		
		packageAdmin.refreshPackages(allBundles.toArray(new Bundle[allBundles.size()]));
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.PluginManager#listPlugins()
	 */
	@Override
	public Collection<Plugin> listPlugins() {
		return new ArrayList<Plugin>(plugins);
	}

	@Override
	public Plugin getPlugin(String pluginName, String pluginVersion) {
		Collection<Plugin> plugins = listPlugins();
		for (Plugin plugin : plugins) {
			String symbolicName = plugin.getIdentifier();
			String version = plugin.getVersion();
			if (symbolicName != null && symbolicName.equals(pluginName) && 
					version != null && version.equals(pluginVersion)) {
				return plugin;
			}
		}
		return null;
	}

	@Override
	public Plugin installPlugin(URL copyFromUrl) throws PluginException {
		String pluginFileName = getFileNameFromUrl(copyFromUrl);
		
		try {
			return installPlugin(pluginFileName, copyFromUrl.openStream());
		} catch (IOException e) {
			throw new PluginException("Unable to open stream to read plugin from " + copyFromUrl.toString());
		}
	}

	@Override
	public Plugin installPlugin(String pluginFileName, InputStream inputStream) throws PluginException {
		File pluginFile = new File(pluginLocation, pluginFileName);
		
		// Require uninstallation if plugin is already installed
		if (pluginFile.exists()) {
			throw new PluginException("Plugin at " + pluginFile.getAbsolutePath() + " already exists, please uninstall it first");
		}
		
		// Copy JAR to plugin directory
		copyPlugin(inputStream, pluginFile);
		
		pluginDescriptor.notifyInstalled(pluginFileName);
		
		return startInstalledPlugin(pluginFile);
	}
	
	@Override
	public void startPlugin(BundlePlugin plugin) throws PluginException {
		Bundle bundle = plugin.getBundle();
		if (bundle.getState() != Bundle.ACTIVE) {
			try {
				bundle.start();
				
				log.info("Plugin " + plugin.getIdentifier() + " has been started");
			} catch (BundleException e) {
				throw new PluginException("Unable to start plugin " + plugin.getIdentifier(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.PluginManager#stopPlugin(java.lang.String)
	 */
	@Override
	public void stopPlugin(BundlePlugin plugin) throws PluginException {
		Bundle bundle = plugin.getBundle();
		if (bundle.getState() == Bundle.ACTIVE) {
			try {
				bundle.stop();
				
				log.info("Plugin " + plugin.getIdentifier() + " has been stopped");
			} catch (BundleException e) {
				throw new PluginException("Unable to stop plugin " + plugin.getIdentifier(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.plugin.PluginManager#removePlugin(java.lang.String)
	 */
	@Override
	public void removePlugin(BundlePlugin plugin) throws PluginException {
		Bundle bundle = plugin.getBundle();
		try {
			// Uninstall bundle
			bundle.uninstall();
			plugins.remove(plugin);
			
			// Request to delete plugin from plugin directory
			File jar = plugin.getJar();
			scheduleForDeletion(jar);

			pluginDescriptor.notifyUninstalled(jar.getName());
			
			// Refresh packages that have been exported by the bundle
			// This triggers a PACKAGES_REFRESHED event which is handled
			// in frameworkEvent() and deletes JARs scheduled for deletion
			// after their packages have been unwired.
			packageAdmin.refreshPackages(new Bundle[] { bundle });
			
			log.info("Plugin " + plugin.getIdentifier() + " has been uninstalled, JAR scheduled for deletion after refresh");
		} catch (BundleException e) {
			throw new PluginException("Unable to remove plugin " + plugin.getIdentifier(), e);
		}
	}

	@Override
	public void frameworkEvent(FrameworkEvent event) {
        if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED) {
        	deleteScheduledFiles();
        }
	}

	private Plugin startInstalledPlugin(File pluginFile) throws PluginException {
		try {
			URL pluginFileUrl = urlForFile(pluginFile);
			Bundle bundle = bundleContext.installBundle(pluginFileUrl.toExternalForm());
			
			// Register installed plugin
			BundlePlugin plugin = new BundlePlugin(bundle, pluginFile, this);
			plugins.add(plugin);
			
			// Start plugin
			startPlugin(plugin);
			
			return plugin;
		} catch (BundleException e) { 
			// TODO: Parse bundle dependency problems into user-friendly error messages
			throw new PluginException("Unable to install bundle at " + pluginFile.getAbsolutePath(), e);
		}
	}

	private void scheduleForDeletion(File jar) {
		synchronized (scheduledForDeletion) {
			scheduledForDeletion.add(jar);
		}
	}

	private static URL urlForFile(File pluginFile) {
		try {
			return pluginFile.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static void copyPlugin(InputStream stream, File pluginFile) throws PluginException {
		try {
			FileUtils.copyInputStreamToFile(stream, pluginFile);
		} catch (IOException e) {
			throw new PluginException("Unable to copy plugin to " + pluginFile.getAbsolutePath());
		}
	}

	private static String getFileNameFromUrl(URL copyFromUrl) {
		String path = copyFromUrl.getPath();
		int lastSlash = path.lastIndexOf('/');
		if (lastSlash > 0) {
			path = path.substring(lastSlash + 1);
		}
		return path;
	}
}
