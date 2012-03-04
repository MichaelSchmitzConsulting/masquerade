package masquerade.sim.app.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import masquerade.sim.app.ui.UploadFileWindow.UploadResultListener;
import masquerade.sim.plugin.Plugin;
import masquerade.sim.plugin.Plugin.State;
import masquerade.sim.plugin.PluginException;
import masquerade.sim.plugin.PluginManager;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Dialog for managing plugins
 */
@SuppressWarnings("serial")
public class PluginDialog extends Window {

	private StatusLog log = StatusLogger.get(PluginDialog.class);
	
	private PluginManager pluginManager;
	
	private Table table;
	private Button installButton;
	private Button uninstallButton;
	private Button stopButton;
	private Button startButton;
	
	/**
	 * Show plugin dialog
	 * @param parent Window to attach the dialog to
	 * @param pluginManager 
	 */
	public static void showModal(Window parent, PluginManager pluginManager) {
		PluginDialog window = new PluginDialog(pluginManager);
		WindowUtil.getRoot(parent).addWindow(window);
	}

	private PluginDialog(final PluginManager pluginManager) {
		super("Plugins");
		
		this.pluginManager = pluginManager;

		setModal(true);
		setWidth("600px");
		setHeight("500px");
		buildLayout();
		
		// Set table contents
		refreshTable();
		
		// Provide stop functionality
		stopButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				Plugin plugin = getSelectedPlugin();
				stopPlugin(plugin);
			}
		});

		// Provide start functionality
		startButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				Plugin plugin = getSelectedPlugin();
				startPlugin(plugin);
			}
		});
		
		// Provide uninstall functionality
		uninstallButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				Plugin plugin = getSelectedPlugin();
				uninstallPlugin(plugin);
			}
		});
		
		// Provide install functionality
		installButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				uploadAndInstall();
			}
		});
		
		// Wire button enablement to table selection
        table.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Plugin selection = (Plugin) event.getProperty().getValue();
				refreshButtonStates(selection);
			}
		});
	}

	private void uploadAndInstall() {
		UploadResultListener listener = new UploadResultListener() {
			@Override public void onUploadFailed() {
				// Ignore, upload window show error notification
			}
			@Override public void onUploadDone(File file) {
				URL url;
				try {
					url = file.toURI().toURL();
					Plugin plugin = pluginManager.installPlugin(url);
					
					// Refresh table to show new plugin
					refreshTable();
					
					// Select uploaded plugin in table
					table.setValue(plugin);
				} catch (MalformedURLException e) {
					WindowUtil.showErrorNotification(getWindow(), "Bad File URL Error", e);
				} catch (PluginException e) {
					WindowUtil.showErrorNotification(getWindow(), "Plugin could not be installed", e);
					log.error("Plugin could not be installed", e);
				} finally {
					file.delete();
				}
			}
		};
		
		ServletContext context = ((WebApplicationContext) getApplication().getContext()).getHttpSession().getServletContext();
		File uploadTargetDir = (File) context.getAttribute("javax.servlet.context.tempdir");
		UploadFileWindow.showModal(getWindow(), "Install Plugin", uploadTargetDir, listener);
	}

	private void refreshTable() {
		table.setContainerDataSource(new BeanItemContainer<Plugin>(Plugin.class, pluginManager.listPlugins()));
		table.setVisibleColumns(new String[] { "identifier", "state", "version", "description" });
	}

	private void buildLayout() {
		VerticalLayout layout = (VerticalLayout) getContent();
		layout.setSpacing(true);
		layout.setWidth("100%");
		layout.setHeight("100%");

		// Title
		Label label = new Label("Installed Plugins");
		layout.addComponent(label);
		
		// Plugin list
        table = new Table(null, null);
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setImmediate(true);
        table.setNullSelectionAllowed(true);
        table.setSizeFull();
        layout.addComponent(table);
        layout.setExpandRatio(table, 1.0f);
        
        // Install/Start/Stop/Uninstall buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);

        startButton = new Button("Start");
        startButton.setEnabled(false);
        buttonLayout.addComponent(startButton);
        
        stopButton = new Button("Stop");
        stopButton.setEnabled(false);
        buttonLayout.addComponent(stopButton);

        uninstallButton = new Button("Uninstall");
        uninstallButton.setEnabled(false);
        buttonLayout.addComponent(uninstallButton);
        
        installButton = new Button("Install...");
        buttonLayout.addComponent(installButton);
        
        layout.addComponent(buttonLayout);
	}

	private void refreshButtonStates(Plugin selection) {
		if (selection != null) {
			boolean started = selection.getState() == State.STARTED;
	
			startButton.setEnabled(!started);
			uninstallButton.setEnabled(!started);
			stopButton.setEnabled(started);
		} else {
			startButton.setEnabled(false);
			stopButton.setEnabled(false);
			uninstallButton.setEnabled(false);
		}
	}

	private void stopPlugin(Plugin plugin) {
		try {
			plugin.stop();
		} catch (PluginException e) {
			WindowUtil.showErrorNotification(getWindow(), "Unable to stop plugin", e.getMessage());
			log.error("Unable to stop plugin", e);
		}
		
		refreshTable();
		table.setValue(plugin);
	}

	private void startPlugin(Plugin plugin) {
		try {
			plugin.start();
		} catch (PluginException e) {
			WindowUtil.showErrorNotification(getWindow(), "Unable to start plugin", e.getMessage());
			log.error("Unable to start plugin", e);
		}
		
		refreshTable();
		table.setValue(plugin);
	}

	private void uninstallPlugin(Plugin plugin) {
		try {
			plugin.remove();
		} catch (PluginException e) {
			WindowUtil.showErrorNotification(getWindow(), "Unable to uninstall plugin", e.getMessage());
			log.error("Unable to uninstall plugin", e);
		}
		
		refreshTable();
		table.setValue(plugin);
	}

	private Plugin getSelectedPlugin() {
		return (Plugin) table.getValue();
	}
}
