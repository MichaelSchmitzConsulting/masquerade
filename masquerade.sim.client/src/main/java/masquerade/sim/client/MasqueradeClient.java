package masquerade.sim.client;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import masquerade.sim.model.SimulationStep;

public interface MasqueradeClient {
	/**
	 * @return List of requests for which the id matches the prefix. Empty list if none found.
	 * @exception MasqueradeClientException
	 */
    List<Request> listRequests(String requestIdPrefix);
    
    /**
     * Uploads a dynamic response script to the simulator
	 * @exception MasqueradeClientException
     */
    void dynamicResponseScript(String requestId, List<SimulationStep> steps);
    
    /**
     * Removes all dynamic response scripts from the simulator matching the prefix (if any)
	 * @exception MasqueradeClientException
     */
    void removeResponseScripts(String requestIdPrefix);
    
    /**
     * Uploads configuration properties, replacing the configuration properties
     * currently set in the server's settings.
	 * @exception MasqueradeClientException
     */
    void setConfigurationProperties(Map<String, String> properties);
    
    /**
     * Returns the current configuration properties from the server's settings
	 * @exception MasqueradeClientException
     */
    Map<String, String> getConfigurationProperties();

    /**
     * Activate a  request mapping on a channel
     * @param mappingName
     * @param channelName
     */
    void activateMappingOnChannel(String mappingName, String channelName);

    /**
     * List active mappings by name on a channel
     */
    List<String> listMappingsOnChannel(String channelName);
    
    /**
     * Post a request to the built-in HTTP channel available under &lt;baseURL&gt;/request/&lt;path&gt;
     * @return {@link InputStream} containing the response content. Clients must close the returned stream.
     */
    InputStream httpChannelRequest(String path, String content);
}
