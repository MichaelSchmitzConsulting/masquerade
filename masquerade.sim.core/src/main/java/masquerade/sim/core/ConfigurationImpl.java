package masquerade.sim.core;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleContext;

import masquerade.sim.model.config.Configuration;

/**
 * {@link Configuration} implementation reading configuration properties
 * from {@link BundleContext} properties.
 */
public class ConfigurationImpl implements Configuration {

	// Matches property names in WebappLifecycleListener which is not available in the OSGi bundle classpath
	private static final String ARTIFACT_ROOT_LOCATION = "masquerade.configuration.artifactRootLocation";
	private static final String REQUEST_LOG_DIR = "masquerade.configuration.requestLogDir";
	private static final String PLUGIN_LOCATION = "masquerade.configuration.pluginLocation";
	private static final String MASQUERADE_VERSION = "masquerade.version";
	private static final String MASQUERADE_BUILD_TIMESTAMP = "masquerade.build.timestamp";
	
	private final File artifactRootLocation;
	private final File requestLogDir;
	private final File pluginLocation;

	private final String masqueradeVersion;
	private final String masqueradeBuildTimestamp;
	
	/**
	 * @param bundleContext
	 */
	public ConfigurationImpl(BundleContext bundleContext) {
		artifactRootLocation = new File(bundleContext.getProperty(ARTIFACT_ROOT_LOCATION));
		requestLogDir = new File(bundleContext.getProperty(REQUEST_LOG_DIR));
		pluginLocation = new File(bundleContext.getProperty(PLUGIN_LOCATION));
		
		masqueradeVersion = bundleContext.getProperty(MASQUERADE_VERSION);
		masqueradeBuildTimestamp = bundleContext.getProperty(MASQUERADE_BUILD_TIMESTAMP);
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.model.config.Configuration#getArtifactRootLocation()
	 */
	@Override
	public File getArtifactRootLocation() throws IOException {
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

	/**
	 * @return
	 */
	public File getRequestLogDir() {
		return requestLogDir;
	}

	/**
	 * @return
	 */
	public File getPluginLocation() {
		return pluginLocation;
	}

}
