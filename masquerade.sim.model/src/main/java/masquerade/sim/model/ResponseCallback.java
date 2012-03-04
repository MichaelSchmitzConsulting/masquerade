package masquerade.sim.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Callback used to send responses to an {@link OutputStream}
 */
public interface ResponseCallback {
	void withResponse(OutputStream responseContent) throws IOException;
}
