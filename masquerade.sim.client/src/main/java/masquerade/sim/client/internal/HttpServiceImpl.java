package masquerade.sim.client.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import masquerade.sim.client.HttpService;
import masquerade.sim.client.MasqueradeClientException;

import org.apache.commons.io.IOUtils;

public class HttpServiceImpl implements HttpService {

	private final URL baseURL;

	public HttpServiceImpl(String baseUrl) {
		this.baseURL = parseUrl(baseUrl);
	}

	private static URL parseUrl(String baseUrl) {
		if (baseUrl == null) {
			throw new IllegalArgumentException("Base URL is required");
		}
		
		try {
			String baseUrlWithSlash = addTrailingSlash(baseUrl);
			return new URL(baseUrlWithSlash);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Base URL is invalid: " + baseUrl, e);
		}
	}

	private static String addTrailingSlash(String baseUrl) {
		return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
	}

	@Override
	public String get(String path) {
		InputStream stream = getStream(path);
		try {
			return IOUtils.toString(stream);
		} catch (IOException e) {
			throw new MasqueradeClientException("Unable to GET URL " + baseURL + path, e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Override
	public InputStream getStream(String path) {
		URL url = buildUrl(path);
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			return connection.getInputStream();
		} catch (IOException e) {
			String errMsg = readFailureResponse(connection);
			throw new MasqueradeClientException("Unable to GET URL " + url.toString() + ", error message = '" + errMsg + "'", e);
		}
	}

	private static String readFailureResponse(HttpURLConnection connection) {
		String errMsg = "";
		if (connection != null) {
			try {
				InputStream errorStream = connection.getErrorStream();
				if (errorStream != null) {
					errMsg = IOUtils.toString(errorStream);
				}
			} catch (IOException ex) {
				// Ignore, return empty error message
			}
		}
		return errMsg;
	}

	@Override
	public void post(String path) {
		post(path, "");
	}

	@Override
	public void post(String path, final String content) {
		UrlWriter writer = new UrlStringWriter(content);
		postToUrl(writer, path).disconnect();
	}

	@Override
	public void post(String path, final InputStream content) {
		UrlWriter writer = new UrlWriter() {
			@Override public void writeTo(HttpURLConnection connection, OutputStream out) throws IOException {
				IOUtils.copy(content, out);
			}
		};
		post(path, writer);
	}

	@Override
	public void post(String path, UrlWriter writer) {
		postToUrl(writer, path).disconnect();		
	}

	public interface UrlWriter {
		void writeTo(HttpURLConnection connection, OutputStream out) throws IOException;
	}

	private HttpURLConnection postToUrl(UrlWriter writer, String path) {
		URL url = buildUrl(path);
		try {
			return writeToUrl(writer, url);
		} catch (IOException e) {
			throw new MasqueradeClientException("Unable to POST to URL " + url.toString(), e);
		}
	}

	private static HttpURLConnection writeToUrl(UrlWriter writer, URL url) throws IOException {
		HttpURLConnection connection = connectTo(url, "POST");
		try {
			OutputStream stream = connection.getOutputStream();
			try {
				writer.writeTo(connection, stream);
			} finally {
				stream.close();
			}
			handleFailures(url, connection);
			return connection;
		} catch (IOException ex) {
			connection.disconnect();
			throw ex;
		}
	}

	private static HttpURLConnection connectTo(URL url, String method) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);
		connection.setDoOutput(true);
		return connection;
	}

	private static void handleFailures(URL url, HttpURLConnection connection) throws IOException {
		int responseCode = connection.getResponseCode();
		if (responseCode != HttpURLConnection.HTTP_OK) {
			InputStream errorStream = connection.getErrorStream();
			String errorMsg = "";
			if (errorStream != null) {
				errorMsg = IOUtils.toString(errorStream);
			}
			connection.disconnect();
			throw new MasqueradeClientException("Failed to post to URL " + url.toString() + ", response code = " + responseCode + ", error message = '"
					+ errorMsg + "'");
		}
	}

	private URL buildUrl(String path) {
		try {
			return new URL(baseURL, path);
		} catch (MalformedURLException e) {
			throw new MasqueradeClientException("Malformed URL (base=" + baseURL + ", path=" + path + ")", e);
		}
	}

	@Override
	public InputStream postAndReceive(String path, String content) {
		HttpURLConnection connection = postToUrl(new UrlStringWriter(content), path);
		try {
			return connection.getInputStream();
		} catch (IOException e) {
			connection.disconnect();
			throw new MasqueradeClientException("Unable to read response", e);
		}
	}

	private static class UrlStringWriter implements UrlWriter {
		private final String content;

		private UrlStringWriter(String content) {
			this.content = content;
		}

		@Override public void writeTo(HttpURLConnection connection, OutputStream out) throws IOException {
			IOUtils.write(content, out);
		}
	}

	@Override
	public void delete(String path) {
		URL url = buildUrl(path);
		HttpURLConnection connection = null; 
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			handleFailures(url, connection);
		} catch (IOException e) {
			throw new MasqueradeClientException("Unable to DELETE to url " + url, e);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
