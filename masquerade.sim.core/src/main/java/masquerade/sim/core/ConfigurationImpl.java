package masquerade.sim.core;

import java.io.File;

import masquerade.sim.model.config.Configuration;

import org.osgi.framework.BundleContext;

/**
 * {@link Configuration} implementation reading configuration properties
 * from {@link BundleContext} properties.
 */
public class ConfigurationImpl implements Configuration {

	// Matches property names in WebappLifecycleListener which is not available in the OSGi bundle classpath
	private static final String MODEL_PERSISTENCE_LOCATION = "masquerade.configuration.modelPersistenceLocation";
	private static final String SETTINGS_PERSISTENCE_LOCATION = "masquerade.configuration.settingsPersistenceLocation";
	private static final String ARTIFACT_ROOT_LOCATION = "masquerade.configuration.artifactRootLocation";
	private static final String REQUEST_LOG_DIR = "masquerade.configuration.requestLogDir";
	private static final String PLUGIN_LOCATION = "masquerade.configuration.pluginLocation";
	private static final String MASQUERADE_VERSION = "masquerade.version";
	private static final String MASQUERADE_BUILD_TIMESTAMP = "masquerade.build.timestamp";
	
	private final File modelPersistenceLocation;
	private final File settingsPersistenceLocation;
	private final File artifactRootLocation;
	private final File requestLogDir;
	private final File pluginLocation;

	private final String masqueradeVersion;
	private final String masqueradeBuildTimestamp;
	
	/**
	 * @param bundleContext
	 */
	public ConfigurationImpl(BundleContext bundleContext) {
		modelPersistenceLocation = new File(bundleContext.getProperty(MODEL_PERSISTENCE_LOCATION));
		settingsPersistenceLocation = new File(bundleContext.getProperty(SETTINGS_PERSISTENCE_LOCATION));
		artifactRootLocation = new File(bundleContext.getProperty(ARTIFACT_ROOT_LOCATION));
		requestLogDir = new File(bundleContext.getProperty(REQUEST_LOG_DIR));
		pluginLocation = new File(bundleContext.getProperty(PLUGIN_LOCATION));
		
		masqueradeVersion = bundleContext.getProperty(MASQUERADE_VERSION);
		masqueradeBuildTimestamp = bundleContext.getProperty(MASQUERADE_BUILD_TIMESTAMP);
	}

	@Override
	public File getModelPersistenceLocation() {
		return modelPersistenceLocation;
	}
	
	@Override
	public File getSettingsPersistenceLocation() {
		return settingsPersistenceLocation;
	}

	@Override
	public File getArtifactRootLocation() {
		return artifactRootLocation;
	}

	@Override
	public String getMasqueradeVersion() {
		return masqueradeVersion;
	}

	@Override
	public String getMasqueradeBuildTimestamp() {
		return masqueradeBuildTimestamp;
	}

	public File getRequestLogDir() {
		return requestLogDir;
	}

	public File getPluginLocation() {
		return pluginLocation;
	}
}
