package masquerade.sim.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.core.converter.CompoundConverter;
import masquerade.sim.core.history.InMemoryRequestHistoryStorage;
import masquerade.sim.core.history.RequestHistoryCleanupJob;
import masquerade.sim.core.history.RequestHistoryImpl;
import masquerade.sim.core.persistence.XmlModelPersistence;
import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.Settings;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.config.Configuration;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.impl.FileLoaderImpl;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.repository.ModelPersistenceService;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.repository.impl.ModelRepositoryImpl;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.model.response.impl.ResponseProviderImpl;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.BundleClassLoader;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * Handles application startup/shutdown.
 */
@Component(immediate=true)
public class CoreInitializer {

	private static final StatusLog log = StatusLogger.get(CoreInitializer.class);

	@Reference protected PluginRegistry pluginRegistry;
	@Reference protected PackageAdmin osgiPackageAdmin;
	
	private ApplicationContext context;
	private Collection<ServiceRegistration> registrations = new ArrayList<ServiceRegistration>();

	private InternalPluginManager pluginManager; 
	
	/**
	 * Application startup, initialize database, configuration and all channel listeners
	 */
	@Activate
	public void activate(ComponentContext componentContext) throws Exception {
		try {
			BundleContext bundleContext = componentContext.getBundleContext();
			
			ConfigurationImpl configuration = new ConfigurationImpl(bundleContext);
			log.info("Starting masquerade simulator v" + configuration.getMasqueradeVersion() + " (Build " + configuration.getMasqueradeBuildTimestamp() + ")");

			// Determine request log and plugin location (done up here to fail early in case this throws an IOException)
			File requestLogDir = configuration.getRequestLogDir();
			log.info("Request log location: " + requestLogDir.getAbsolutePath());
			File pluginLocation = configuration.getPluginLocation();
			log.info("Plugin location: " + pluginLocation.getAbsolutePath());

			// Directories can be accessed, export Configuration as OSGi service 
			registerService(Configuration.class, configuration, bundleContext);
						
			pluginManager = new PluginManagerImpl(pluginLocation, bundleContext, osgiPackageAdmin);
			pluginManager.initialize();
			registerService(PluginManager.class, pluginManager, bundleContext);

			// Create converter
			Converter converter = new CompoundConverter();
			
			// Create file loader and register as OSGi service
			File artifactsRoot = configuration.getArtifactRootLocation();
			FileLoader fileLoader = new FileLoaderImpl(artifactsRoot);
			registerService(FileLoader.class, fileLoader, bundleContext);
			
			// Create configuration variable holder
			ConfigurationVariableHolder configVariableHolder = new ConfigurationVariableHolder(converter); 

			// Create model persistence service for repository
			File modelFile = configuration.getModelPersistenceLocation();
			log.info("Model persistence location: " + modelFile.getAbsolutePath());
			File settingsFile = configuration.getSettingsPersistenceLocation();
			log.info("Settings persistence location: " + settingsFile.getAbsolutePath());
			ModelPersistenceService persistenceService = new XmlModelPersistence(modelFile, settingsFile, pluginRegistry);
			
			// Create repository and publish as OSGi service
			ModelRepositoryImpl modelRepository = new ModelRepositoryImpl(persistenceService);
			modelRepository.load();
			registerService(ModelRepository.class, modelRepository, bundleContext);
			
			// Create request history
			RequestHistory requestHistory = new RequestHistoryImpl(new InMemoryRequestHistoryStorage(), requestLogDir);
			registerService(RequestHistory.class, requestHistory, bundleContext);

			// Create response provider
			ResponseProvider responseProvider = new ResponseProviderImpl();
			registerService(ResponseProvider.class, responseProvider, bundleContext);
			
			// Create simulation runner and register as OSGi service
			SimulationRunner simulationRunner = new SimulationRunnerImpl(modelRepository, requestHistory, converter, fileLoader, configVariableHolder, responseProvider);
			registerService(SimulationRunner.class, simulationRunner, bundleContext);
			
			// Create channel listener registry and register as OSGi service
			ClassLoader bundleClassLoader = new BundleClassLoader(componentContext.getBundleContext().getBundle());
			ChannelListenerRegistry listenerRegistry = new ChannelListenerRegistryImpl(simulationRunner, configVariableHolder, modelRepository, bundleClassLoader);
			registerService(ChannelListenerRegistry.class, listenerRegistry, bundleContext);
			
			// Create history cleanup job
			RequestHistoryCleanupJob cleanupJob = new RequestHistoryCleanupJob(requestHistory);
			
			// Create settings change listener, apply settings and register as OSGi service
			Settings settings = modelRepository.getSettings();
			SettingsChangeListener settingsChangeListener = new AppSettingsChangeListener(cleanupJob, configVariableHolder);
			registerService(SettingsChangeListener.class, settingsChangeListener, bundleContext);
			settingsChangeListener.settingsChanged(Settings.NO_SETTINGS, settings);
			
			// Create application context
			context = new ApplicationContext(listenerRegistry, modelRepository, requestHistory, fileLoader, converter, artifactsRoot, cleanupJob, settingsChangeListener, configVariableHolder);

			// Start channels
			listenerRegistry.startAll();
			
			// Start request history cleanup job
			cleanupJob.start();
			
			log.info("Simulator started");
		} catch (Exception ex) {
			log.error("Error starting Masquerade", ex);
			throw ex;
		}
	}

	/**
	 * Application is being shut down, stop
	 * all listeners and remove the application
	 * context from the servlet context.
	 */
	@Deactivate
	public void deactivate(ComponentContext componentContext) throws Exception {
		log.info("Shutting down masquerade simulator");
		if (context != null) {
			context.getRequestHistoryCleanupJob().stop();
			stopChannels(context.getChannelListenerRegistry());
			context = null;
		}
		
		// Unregister OSGi services
		for (ServiceRegistration serviceRegistration : registrations) {
			serviceRegistration.unregister();
		}
		registrations.clear();
		
		// Uninstall plugins
		if (pluginManager != null) {
			pluginManager.shutdown();
			pluginManager = null;
		}
		
		// Clear request history log files if kept in-memory
		InMemoryRequestHistoryStorage.onShutdown();
	}

	private <T> void registerService(Class<T> serviceInterface, T service, BundleContext bundleContext) {
		ServiceRegistration serviceRegistration = bundleContext.registerService(serviceInterface.getName(), service, null);
		registrations.add(serviceRegistration);
	}

	private static void stopChannels(ChannelListenerRegistry channelListenerRegistry) {
		try {
			channelListenerRegistry.stopAll();
		} catch (Throwable t) {
			log.error("Exception while stopping all channel listeners", t);
		}
	}
}
