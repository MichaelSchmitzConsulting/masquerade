package masquerade.sim.model.importexport;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Serializes simulation model objects.
 */
public interface Exporter {
	void exportModelObject(Object object, OutputStream stream) throws IOException;
}

