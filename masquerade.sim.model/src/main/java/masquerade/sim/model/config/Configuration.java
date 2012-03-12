package masquerade.sim.model.config;

import java.io.File;
import java.io.IOException;

/**
 * Interface for a service providing configuration values to bundles
 */
public interface Configuration {
	
	/**
	 * Reads the artifact directory location setting from the system property
	 * <code>masquerade.artifact.dir</code>, or places it in the ${masquerade.home}
	 * if not set.
	 * 
	 * @return Where the masquerade request log directory should be located
	 * @throws IOException If the directory cannot be accessed or created
	 */
	File getArtifactRootLocation() throws IOException;
	
	String getMasqueradeVersion();
	
	String getMasqueradeBuildTimestamp();

	/**
	 * @return Location of the persistent store, usually a file containing persistent model objects e.g. as XML
	 */
	File getModelPersistenceLocation();

	/**
	 * @return Location of the persistent store, usually a file containing persistent settings
	 */
	File getSettingsPersistenceLocation();
}
