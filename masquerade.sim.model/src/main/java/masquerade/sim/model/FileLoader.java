package masquerade.sim.model;

import java.io.InputStream;
import java.util.Collection;

public interface FileLoader {
	/**
	 * Loads a file (e.g. a template) of the given name
	 * @param type
	 * @param name
	 * @return {@link InputStream} for the file, or <code>null</code> if not found
	 */
	InputStream load(FileType type, String name);
	
	Collection<String> listTemplates(FileType type);
}
