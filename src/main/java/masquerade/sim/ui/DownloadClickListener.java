package masquerade.sim.ui;

import java.io.IOException;

import masquerade.sim.util.WindowUtil;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

public class DownloadClickListener implements ClickListener {

	private Window window;
	private DownloadHandler downloadHandler;

	public DownloadClickListener(Window window, DownloadHandler downloadHandler) {
		this.window = window;
		this.downloadHandler = downloadHandler;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		try {
			window.open(downloadHandler.provideDownload());
		} catch (IOException e) {
			WindowUtil.showErrorNotification(window, "Error creating download", e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
