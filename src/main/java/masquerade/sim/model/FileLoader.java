package masquerade.sim.model;

import java.io.InputStream;


public interface FileLoader {
	InputStream load(FileType type, String name);
}
