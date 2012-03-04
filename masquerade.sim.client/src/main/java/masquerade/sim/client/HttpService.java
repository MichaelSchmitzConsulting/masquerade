package masquerade.sim.client;

import java.io.InputStream;
import java.io.OutputStream;

import masquerade.sim.client.internal.HttpServiceImpl.UrlWriter;

/**
 * Abstracts HTTP operations for mocking/stubbing in tests
 */
public interface HttpService {
	String get(String path);

	/**
	 * GET to stream. Clients must close the returned {@link InputStream}.
	 */
	InputStream getStream(String path);
	
	/**
	 * POST {@link String}, ensure OK error code
	 */
	void post(String path, String content);

	/**
	 * Issue POST request with empty content, ensure OK error code
	 */
	void post(String path);
	
	/**
	 * POST from {@link InputStream}, ensure OK error code
	 */
	void post(String path, InputStream content);

	/**
	 * POST to an {@link OutputStream} using an {@link UrlWriter} callback
	 */
	void post(String path, UrlWriter writer);
	
	/**
	 * POST a request, receive an {@link InputStream} for the response.
	 * Clients must close the returned {@link InputStream}.
	 */
	InputStream postAndReceive(String path, String content);
}
