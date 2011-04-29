package masquerade.sim;

import java.io.File;

import masquerade.sim.channel.ChannelListenerRegistry;
import masquerade.sim.db.DatabaseLifecycle;
import masquerade.sim.db.ModelRepositoryFactory;
import masquerade.sim.db.RequestHistoryCleanupJob;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;
import masquerade.sim.model.NamespaceResolver;

/**
 * Dependency injection for application-wide objects created during initialization.
 */
public class ApplicationContext {

	private DatabaseLifecycle modelDbLifecycle;
	private DatabaseLifecycle historyDbLifecycle;
	private ChannelListenerRegistry channelListenerRegistry;
	private RequestHistoryFactory requestHistoryFactory;
	private ModelRepositoryFactory modelRepositoryFactory;
	private FileLoader fileLoader;
	private Converter converter;
	private File artifactRoot;
	private NamespaceResolver namespaceResolver;
	private RequestHistoryCleanupJob cleanupJob;

	/**
	 * @param databaseLifecycle
	 * @param channelListenerRegistry
	 * @param requestHistoryFactory
	 * @param modelRepositoryFactory
	 * @param fileLoader
	 * @param converter
	 * @param artifactRoot
	 * @param namespaceResolver
	 * @param cleanupJob 
	 */
	public ApplicationContext(DatabaseLifecycle modelDbLifecycle, DatabaseLifecycle historyDbLifecycle, ChannelListenerRegistry channelListenerRegistry,
		RequestHistoryFactory requestHistoryFactory, ModelRepositoryFactory modelRepositoryFactory, FileLoader fileLoader, Converter converter,
		File artifactRoot, NamespaceResolver namespaceResolver, RequestHistoryCleanupJob cleanupJob) {
		this.modelDbLifecycle = modelDbLifecycle;
		this.historyDbLifecycle = historyDbLifecycle;
		this.channelListenerRegistry = channelListenerRegistry;
		this.requestHistoryFactory = requestHistoryFactory;
		this.modelRepositoryFactory = modelRepositoryFactory;
		this.fileLoader = fileLoader;
		this.converter = converter;
		this.artifactRoot = artifactRoot;
		this.namespaceResolver = namespaceResolver;
		this.cleanupJob = cleanupJob;
	}

	/**
	 * @return The {@link DatabaseLifecycle} object providing access to the root DB session, not meant for public usage - factories
	 *         usually provide wrapper objecst for DB access that use their own session for safe concurrent usage.
	 */
	DatabaseLifecycle getModelDb() {
		return modelDbLifecycle;
	}

	/**
	 * @return The {@link DatabaseLifecycle} object providing access to the root DB session, not meant for public usage - factories
	 *         usually provide wrapper objecst for DB access that use their own session for safe concurrent usage.
	 */
	DatabaseLifecycle getHistoryDb() {
		return historyDbLifecycle;
	}

	RequestHistoryCleanupJob getRequestHistoryCleanupJob() {
		return cleanupJob;
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
	public RequestHistoryFactory getRequestHistoryFactory() {
		return requestHistoryFactory;
	}

	/**
	 * @return The {@link ModelRepositoryFactory}
	 */
	public ModelRepositoryFactory getModelRepositoryFactory() {
		return modelRepositoryFactory;
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

	public NamespaceResolver getNamespaceResolver() {
		return namespaceResolver;
	}
}
