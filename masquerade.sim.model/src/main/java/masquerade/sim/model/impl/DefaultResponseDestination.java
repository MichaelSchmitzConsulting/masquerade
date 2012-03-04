package masquerade.sim.model.impl;

import java.io.OutputStream;

import masquerade.sim.model.ResponseCallback;
import masquerade.sim.model.ResponseDestination;

/**
 * Default response destination implementation, simply delegates to an {@link OutputStream}
 * and does not support intermediate responses.
 */
public class DefaultResponseDestination implements ResponseDestination {

	private final OutputStream outputStream;

	public DefaultResponseDestination(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public OutputStream getResponseOutputStream() {
		return outputStream;
	}

	/**
	 * Default implementation does not support intermediate responses
	 * @throws Exception 
	 * @exception UnsupportedOperationException
	 */
	@Override
	public void sendIntermediateResponse(ResponseCallback callback) throws Exception {
		throw new UnsupportedOperationException("This response channel does not support intermediate responses");
	}
}
