package masquerade.sim.app;

import static masquerade.sim.app.ui.Icons.LISTENER;
import static masquerade.sim.app.ui.Icons.SIMULATION;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import masquerade.sim.app.ui.MainLayout;
import masquerade.sim.app.ui2.factory.ChannelFactory;
import masquerade.sim.app.ui2.factory.SimulationFactory;
import masquerade.sim.app.ui2.factory.impl.ChannelFactoryImpl;
import masquerade.sim.app.ui2.factory.impl.SimulationFactoryImpl;
import masquerade.sim.app.ui2.presenter.ChannelPresenter;
import masquerade.sim.app.ui2.presenter.SimulationPresenter;
import masquerade.sim.app.ui2.view.impl.ChannelViewImpl;
import masquerade.sim.app.ui2.view.impl.SimulationViewImpl;
import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.config.Configuration;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.listener.SettingsChangeListener;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.settings.ModelSettingsProvider;
import masquerade.sim.model.settings.SettingsProvider;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Window;

/**
 * Application entry point
 */
public class MasqueradeApplication extends Application {
	private static final long serialVersionUID = 1L;
	
	private static final StatusLog log = StatusLogger.get(MasqueradeApplication.class);
	
	private RequestHistory requestHistory;
	private String baseUrl;
	private Resource logo;
	private File artifactRoot;
	private ModelRepository modelRepository;

	@Override
	public void init() {
		AppServiceLocator serviceLocator = getServiceLocator();
		
		modelRepository = serviceLocator.getModelRepository();
		requestHistory = serviceLocator.getRequestHistory();
				
		baseUrl = ((WebApplicationContext)getContext()).getHttpSession().getServletContext().getContextPath();
		String imgUrl = baseUrl + "/res/logo.png";
    	logo = new ExternalResource(imgUrl);
		
		try {
			artifactRoot = serviceLocator.getConfiguration().getArtifactRootLocation();
		} catch (IOException e) {
			log.error("Unable to determine artifact root: ", e);
		}
    	
		Window mainWindow = createNewMainWindow();
		setMainWindow(mainWindow);
		
		createMainWindowLayout(mainWindow);	
	}
	
	/**
	 * Overridden to add support for Browser multi-tab/window instances
	 */
	@Override
	public Window getWindow(String name) {
		// If the window is identified by name, we are good to go
		Window w = super.getWindow(name);

		// If not, we must create a new window for this new browser window/tab
		if (w == null) {
			w = createNewMainWindow();
			createMainWindowLayout(w);

			// Use the random name given by the framework to identify this
			// window in future
			w.setName(name);
			addWindow(w);

			// Move to the url to remember the name in the future
			w.open(new ExternalResource(w.getURL()));
		}
		
		return w;
	}

	@Override
	public void close() {
		super.close();
	
		modelRepository = null;
		artifactRoot = null;
		requestHistory = null;
		baseUrl = null;
		logo = null;
	}

	private Window createNewMainWindow() {
		return new Window("Masquerade Simulator");
	}
	
	private void createMainWindowLayout(Window mainWindow) {
		final AppServiceLocator serviceLocator = getServiceLocator();
		SimulationRunner simulationRunner = serviceLocator.getSimulationRunner();
		SettingsChangeListener settingsChangeListener = serviceLocator.getSettingsChangeListener();
		PluginManager pluginManager = serviceLocator.getPluginManager();
		PluginRegistry pluginRegistry = serviceLocator.getPluginRegistry();
		ChannelListenerRegistry channelListenerRegistry = serviceLocator.getChannelListenerRegistry();
		FormFieldFactory fieldFactory = serviceLocator.getFieldFactory();
		
		SettingsProvider settingsProvider = new ModelSettingsProvider(modelRepository);
		
		// TODO: Refactor MainLayput for not to contain any tab content/logic
		MainLayout mainLayout = new MainLayout(logo, requestHistory, artifactRoot, new SendTestRequestActionImpl(simulationRunner),
				settingsChangeListener, baseUrl, pluginManager, 
				settingsProvider, getVersionInformation(serviceLocator.getConfiguration()));

		SimulationFactory simulationFactory = new SimulationFactoryImpl(mainWindow, pluginRegistry, modelRepository, fieldFactory);
		SimulationViewImpl simulations = new SimulationViewImpl(fieldFactory);
		SimulationPresenter simulationPresenter = new SimulationPresenter(simulations, modelRepository, simulationFactory);
		simulations.bind(simulationPresenter);
		mainLayout.addTab(simulations, SIMULATION.icon(baseUrl));
		
		ChannelFactory channelFactory = new ChannelFactoryImpl(pluginRegistry, modelRepository, mainWindow);
		ChannelViewImpl channels = new ChannelViewImpl(fieldFactory);
		ChannelPresenter channelPresenter = new ChannelPresenter(channels, modelRepository, channelFactory, channelListenerRegistry);
		channels.bind(channelPresenter);
		mainLayout.addTab(channels, LISTENER.icon(baseUrl));
		
		mainWindow.setContent(mainLayout);
	}

	private AppServiceLocator getServiceLocator() {
		ServletContext servletContext = ((WebApplicationContext) getContext()).getHttpSession().getServletContext();
		AppServiceLocator serviceLocator = (AppServiceLocator) servletContext.getAttribute(AppServiceLocator.PROP_SERVICE_LOCATOR);
		return serviceLocator;
	}
	
	private static String getVersionInformation(Configuration configuration) {
		return "Masquerade Service Simulator v" + configuration.getMasqueradeVersion() + " (Build " + configuration.getMasqueradeBuildTimestamp() + ")";
	}
}
