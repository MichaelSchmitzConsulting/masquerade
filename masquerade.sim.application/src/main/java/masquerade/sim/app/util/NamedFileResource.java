package masquerade.sim.app.util;

import java.io.File;

import com.vaadin.Application;
import com.vaadin.terminal.FileResource;

/**
 * A {@link FileResource} wiht a custom name and MIME type
 */
@SuppressWarnings("serial")
public class NamedFileResource extends FileResource {
	private final String mimeType;
	private final String fileName;

	public NamedFileResource(File sourceFile, Application application, String mimeType, String fileName) {
		super(sourceFile, application);
		this.mimeType = mimeType;
		this.fileName = fileName;
	}

	@Override
	public String getFilename() {
		return fileName;
	}

	@Override
	public String getMIMEType() {
		return mimeType;
	}
}