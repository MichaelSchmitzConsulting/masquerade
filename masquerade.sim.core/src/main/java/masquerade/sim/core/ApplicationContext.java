package masquerade.sim.core;

import java.io.File;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.core.history.RequestHistoryCleanupJob;
import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.repository.ModelRepository;

/**
 * Dependency injection for application-wide objects created during initialization.
 */
public class ApplicationContext {

	private final ChannelListenerRegistry channelListenerRegistry;
	private final ModelRepository modelRepository;
	private final RequestHistory requestHistory;
	private final FileLoader fileLoader;
	private final Converter converter;
	private final File artifactRoot;
	private final RequestHistoryCleanupJob cleanupJob;
	private final SettingsChangeListener settingsChangeListener;
	private final VariableHolder configVariableHolder;

	public ApplicationContext(ChannelListenerRegistry channelListenerRegistry, ModelRepository modelRepository, RequestHistory requestHistory, FileLoader fileLoader, Converter converter,
		File artifactRoot, RequestHistoryCleanupJob cleanupJob, SettingsChangeListener settingsChangeListener,
		VariableHolder configVariableHolder) {
		this.channelListenerRegistry = channelListenerRegistry;
		this.modelRepository = modelRepository;
		this.requestHistory = requestHistory;
		this.fileLoader = fileLoader;
		this.converter = converter;
		this.artifactRoot = artifactRoot;
		this.cleanupJob = cleanupJob;
		this.settingsChangeListener = settingsChangeListener;
		this.configVariableHolder = configVariableHolder;
	}

	public RequestHistoryCleanupJob getRequestHistoryCleanupJob() {
		return cleanupJob;
	}
	
	public ModelRepository getModelRepository() {
		return modelRepository;
	}

	/**
	 * @return The {@link ChannelListenerRegistry}
	 */
	public ChannelListenerRegistry getChannelListenerRegistry() {
		return channelListenerRegistry;
	}

	/**
	 * @return The {@link RequestHistoryFactory}
	 */
	public RequestHistory getRequestHistory() {
		return requestHistory;
	}

	/**
	 * @return A {@link FileLoader} capable of loading {@link FileType files} (e.g. templates)
	 */
	public FileLoader getFileLoader() {
		return fileLoader;
	}

	/**
	 * @return The {@link Converter} to convert values to desired types
	 */
	public Converter getConverter() {
		return converter;
	}

	/**
	 * @return the artifactRoot
	 */
	public File getArtifactRoot() {
		return artifactRoot;
	}

	/**
	 * @return the settingsChangeListener
	 */
	public SettingsChangeListener getSettingsChangeListener() {
		return settingsChangeListener;
	}

	public VariableHolder getConfigurationVariableHolder() {
		return configVariableHolder;
	}
}
