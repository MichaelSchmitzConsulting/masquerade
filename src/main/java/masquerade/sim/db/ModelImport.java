package masquerade.sim.db;

import java.io.File;

public interface ModelImport {
	void importModel(File file, boolean isReplaceExisting);
}
