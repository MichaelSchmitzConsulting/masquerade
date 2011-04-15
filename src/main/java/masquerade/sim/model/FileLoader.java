package masquerade.sim.model;

import java.io.InputStream;
import java.util.Collection;

public interface FileLoader {
	InputStream load(FileType type, String name);
	
	Collection<String> listTemplates(FileType type);
}
