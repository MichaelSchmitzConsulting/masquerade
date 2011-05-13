package masquerade.sim.db;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import masquerade.sim.ui.DownloadHandler;
import masquerade.sim.util.NamedFileResource;

import com.vaadin.Application;
import com.vaadin.terminal.Resource;

/**
 * Exports the simulation configuration to a temporary
 * file and provides it as a download (using a {@link Resource})
 * to the client.
 */
public class ModelDownloadHandler implements DownloadHandler {

	private final ModelExport exporter;
	private final Application application;

	public ModelDownloadHandler(ModelExport exporter, Application application) {
		this.exporter = exporter;
		this.application = application;
	}

	@Override
	public Resource provideDownload() throws IOException {
		File tempFile = File.createTempFile("masquerade-", "-download");
		exporter.exportModel(tempFile);
		
		// TODO: Limit/cleanup temp files
		tempFile.deleteOnExit();
		
		final String fileName = "masquerade-export." + timestamp() + ".simulation";
		final String mimeType = "application/x-masquerade-simulation";
		
		return new NamedFileResource(tempFile, application, mimeType, fileName);
	}

	private String timestamp() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS").format(new Date());
	}

}
