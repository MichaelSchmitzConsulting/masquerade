package masquerade.sim.app;

import masquerade.sim.ApplicationContext;
import masquerade.sim.ApplicationLifecycle;
import masquerade.sim.SettingsChangeListener;
import masquerade.sim.db.ModelDownloadHandler;
import masquerade.sim.db.ModelExport;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.db.ModelUploadHandler;
import masquerade.sim.history.RequestHistory;
import masquerade.sim.ui.DownloadHandler;
import masquerade.sim.ui.MainLayout;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Window;

/**
 * Application entry point
 */
public class MasqueradeApplication extends Application {
	private static final int MEGABYTES = 1000000;
	private static final long MAX_UPLOAD_SIZE = 100 * MEGABYTES;

	private ModelRepository modelRepository;
	private RequestHistory requestHistory;

	@Override
	public void init() {
		ApplicationContext context = ApplicationLifecycle.getApplicationContext(this);
		
		// Start db session
		modelRepository = context.getModelRepositoryFactory().startModelRepositorySession();
		RequestHistory requestHistory = context.getRequestHistoryFactory().startRequestHistorySession();
		
		// Logo url
    	String baseUrl = ((WebApplicationContext)getContext()).getHttpSession().getServletContext().getContextPath();
		String imgUrl = baseUrl + "/logo.png";
    	Resource logo = new ExternalResource(imgUrl);
		
    	// Simulation configuration upload handler
    	ModelUploadHandler modelUploadHandler = new ModelUploadHandler(context.getModelRepositoryFactory());
    	UploadHandler uploadHandler = new UploadHandlerImpl(modelUploadHandler, MAX_UPLOAD_SIZE);
    	
    	// Simulation configuration download handler
    	ModelExport exporter = context.getModelExport();
    	DownloadHandler downloadHandler = new ModelDownloadHandler(exporter, this);
    	
		// Setup UI
		Window mainWindow = new Window("Masquerade Simulator");
		SettingsChangeListener settingsChangeListener = context.getSettingsChangeListener();
		mainWindow.setContent(new MainLayout(logo, modelRepository, requestHistory, context.getArtifactRoot(), new SendTestRequestAction(context),
				settingsChangeListener, baseUrl, uploadHandler, downloadHandler, modelUploadHandler));
		setMainWindow(mainWindow);
	}

	@Override
	public void close() {
		if (modelRepository != null) {
			modelRepository.endSession();
			modelRepository = null;
		}
		if (requestHistory != null) {
			requestHistory.endSession();
			requestHistory = null;
		}
		super.close();
	}
}
