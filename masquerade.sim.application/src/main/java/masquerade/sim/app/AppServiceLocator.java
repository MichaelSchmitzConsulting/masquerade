package masquerade.sim.app;

import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.config.Configuration;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.ui.FormFieldFactory;

public interface AppServiceLocator {
	String PROP_SERVICE_LOCATOR = AppServiceLocator.class.getName();
	
	ModelRepository getModelRepository();
	RequestHistory getRequestHistory();
	SettingsChangeListener getSettingsChangeListener();
	SimulationRunner getSimulationRunner();
	FormFieldFactory getFieldFactory();
	Configuration getConfiguration();
	PluginManager getPluginManager();
	PluginRegistry getPluginRegistry();
	ChannelListenerRegistry getChannelListenerRegistry();
}
