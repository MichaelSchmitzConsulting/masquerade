package masquerade.sim.app;

import masquerade.sim.ApplicationContext;
import masquerade.sim.ApplicationLifecycle;
import masquerade.sim.db.ModelRepository;
import masquerade.sim.history.RequestHistory;
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
	private ModelRepository modelRepository;
	private RequestHistory requestHistory;

	@Override
	public void init() {
		ApplicationContext context = ApplicationLifecycle.getApplicationContext(this);
		
		// Start db session
		modelRepository = context.getModelRepositoryFactory().startModelRepositorySession();
		RequestHistory requestHistory = context.getRequestHistoryFactory().startRequestHistorySession();
		
		// Logo url
    	String imgUrl = ((WebApplicationContext)getContext()).getHttpSession().getServletContext().getContextPath() + "/logo.png";
    	Resource logo = new ExternalResource(imgUrl);
		
		// Setup UI
		Window mainWindow = new Window("Masquerade Simulator");
		mainWindow.setContent(new MainLayout(logo, modelRepository, requestHistory, context.getArtifactRoot(), new SendTestRequestAction(context)));
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
