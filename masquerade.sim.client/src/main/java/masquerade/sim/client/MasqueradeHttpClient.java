package masquerade.sim.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import masquerade.sim.client.internal.HttpServiceImpl;
import masquerade.sim.client.internal.ListRequestsResponseParser;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.util.ScriptMarshaller;
import masquerade.sim.util.XmlScriptMarshaller;

import org.apache.commons.io.IOUtils;

/**
 * Interface for Masquerade's HTTP API
 */
public class MasqueradeHttpClient implements MasqueradeClient {
	
	private static final String UTF_8 = "UTF-8";

	private static final String API = "api";

	private static final String API_LIST_REQUESTS = API + "/listRequests";

	private static final String API_DEFINE_RESPONSE_SCRIPT = API + "/dynamicResponseScript";

	private static final String API_REMOVE_RESPONSE_SCRIPTS = API + "/removeResponseScripts";

	private static final String API_CONFIGURATION_PROPERTIES = API + "/settings/configurationProperties";
	
	private static final String API_CHANNEL = API + "/channel";

	private static final String HTTP_CHANNEL = "request/";

	private final HttpService httpService;
	
	/**
	 * @param url Masquerade simulator base URL, e.g. http://host:80/masquerade
	 */
	public MasqueradeHttpClient(String baseUrl) {
		this.httpService = new HttpServiceImpl(baseUrl);
	}

	public MasqueradeHttpClient(HttpService httpService) {
		this.httpService = httpService;
	}
	
	@Override
	public List<Request> listRequests(String requestIdPrefix) {
		InputStream stream = httpService.getStream(API_LIST_REQUESTS + "/" + requestIdPrefix);
		try {
			return ListRequestsResponseParser.parse(stream);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Override
	public void dynamicResponseScript(String requestId, List<SimulationStep> steps) {
		ScriptMarshaller marshaller = new XmlScriptMarshaller();
		String content = marshaller.marshal(steps);
		httpService.post(API_DEFINE_RESPONSE_SCRIPT + "/" + requestId, content);
	}

	@Override
	public void removeResponseScripts(String requestIdPrefix) {
		httpService.get(API_REMOVE_RESPONSE_SCRIPTS + "/" + requestIdPrefix);
	}

	@Override
	public void setConfigurationProperties(Map<String, String> properties) {
		String content = convertPropertiesToString(properties);
		httpService.post(API_CONFIGURATION_PROPERTIES, content);
	}

	private String convertPropertiesToString(Map<String, String> properties) {
		Properties props = new Properties();
		props.putAll(properties);
		ByteArrayOutputStream content = new ByteArrayOutputStream();
		try {
			props.store(content, "");
			return content.toString();
		} catch (IOException e) {
			throw new MasqueradeClientException("Unable to convert properties", e);
		} finally {
			IOUtils.closeQuietly(content);
		}
	}

	@Override
	public Map<String, String> getConfigurationProperties() {
		String content = httpService.get(API_CONFIGURATION_PROPERTIES);
		Properties props = convertStringToProperties(content);
		return convertPropertiesToStringMap(props);
	}

	private Map<String, String> convertPropertiesToStringMap(Properties props) {
		Map<String, String> map = new HashMap<String, String>();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			map.put((String) entry.getKey(), (String) entry.getValue()); 
		}
		return map;
	}

	private Properties convertStringToProperties(String content) {
		Properties props = new Properties();
		try {
			props.load(new StringReader(content));
		} catch (IOException e) {
			throw new MasqueradeClientException("Unable to convert properties received from server", e);
		}
		return props;
	}

	@Override
	public void activateMappingOnChannel(String mappingName, String channelName) {
		String url = API_CHANNEL + "/" + urlEncode(channelName) + "/" + urlEncode(mappingName);
		httpService.post(url);
	}

	private String urlEncode(String mappingName) {
		try {
			return URLEncoder.encode(mappingName, UTF_8);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported encoding", e);
		}
	}

	@Override
	public List<String> listMappingsOnChannel(String channelName) {
		String url = API_CHANNEL + "/" + urlEncode(channelName);
		String content = httpService.get(url);
		return Arrays.asList(content.split("(\r)?\n"));
	}

	@Override
	public InputStream httpChannelRequest(String path, String content) {
		return httpService.postAndReceive(HTTP_CHANNEL + removeLeadingSlash(path), content);
	}

	private String removeLeadingSlash(String path) {
		return path.startsWith("/") ? path.substring(1, path.length()) : path;
	}
}
