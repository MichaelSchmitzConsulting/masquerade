package masquerade.sim.model;

import java.io.OutputStream;

/**
 * Response destination provided by a channel to send a response
 * to a an incoming request
 */
public interface ResponseDestination {

	/**
	 * @return {@link OutputStream} for sending the final response
	 */
	OutputStream getResponseOutputStream();
	
	/**
	 * @param callback Callback handling response marshalling to an {@link OutputStream} 
	 * @throws Exception 
	 * @exception UnsupportedOperationException If the channel does not support intermediate responses
	 */
	void sendIntermediateResponse(ResponseCallback callback) throws Exception;
}
