package masquerade.sim.channel.file;

import java.io.File;
import java.io.IOException;

public interface FileProcessor {
	void processFile(File file) throws Exception;
}
