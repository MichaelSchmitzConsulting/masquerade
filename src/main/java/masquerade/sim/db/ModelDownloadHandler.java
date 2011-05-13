package masquerade.sim.db;

import java.io.File;
import java.io.IOException;

import masquerade.sim.ui.DownloadHandler;

import com.vaadin.Application;
import com.vaadin.terminal.FileResource;
import com.vaadin.terminal.Resource;

public class ModelDownloadHandler implements DownloadHandler {

	private ModelExport exporter;
	private Application application;

	public ModelDownloadHandler(ModelExport exporter, Application application) {
		this.exporter = exporter;
		this.application = application;
	}

	@Override
	public Resource provideDownload() throws IOException {
		File tempFile = File.createTempFile("masquerade-", "-download");
		exporter.exportModel(tempFile);
		// TODO: Limit/cleanup temp files
		return new FileResource(tempFile, application);
	}

}
