package masquerade.sim.app;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.config.Configuration;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.importexport.Importer;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.plugin.PluginRegistry;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.vaadin.ui.FormFieldFactory;

@Component
@Service
public class AppServiceLocatorImpl implements AppServiceLocator {

	@Reference protected ModelRepository modelRepository;
	@Reference protected RequestHistory requestHistory;
	@Reference protected SettingsChangeListener settingsChangeListener;
	@Reference protected SimulationRunner simulationRunner;
	@Reference protected PluginRegistry pluginRegistry;
	@Reference protected Configuration configuration;
	@Reference protected FormFieldFactory fieldFactory;
	@Reference protected PluginManager pluginManager;
	@Reference protected ChannelListenerRegistry channelListenerRegistry;
	@Reference protected Importer importer;
	
	@Override
	public ModelRepository getModelRepository() {
		return modelRepository;
	}

	@Override
	public RequestHistory getRequestHistory() {
		return requestHistory;
	}

	@Override
	public SettingsChangeListener getSettingsChangeListener() {
		return settingsChangeListener;
	}

	@Override
	public SimulationRunner getSimulationRunner() {
		return simulationRunner;
	}

	@Override
	public FormFieldFactory getFieldFactory() {
		return fieldFactory;
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	@Override
	public ChannelListenerRegistry getChannelListenerRegistry() {
		return channelListenerRegistry;
	}

	@Override
	public PluginRegistry getPluginRegistry() {
		return pluginRegistry;
	}

	@Override
	public Importer getImpporter() {
		return importer;
	}
}
