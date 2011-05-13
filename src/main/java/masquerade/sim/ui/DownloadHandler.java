package masquerade.sim.ui;

import java.io.IOException;

import com.vaadin.terminal.Resource;

public interface DownloadHandler {
	Resource provideDownload() throws IOException;
}
