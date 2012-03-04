package masquerade.sim.model.response.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import masquerade.sim.model.SimulationStep;
import masquerade.sim.model.response.ResponseProvider;

/**
 * Implementation of {@link ResponseProvider}
 */
public class ResponseProviderImpl implements ResponseProvider {
	/**
	 * Maps request IDs to {@link ProvidedResponse}. Ordered by timestamp, oldest provided 
	 * responses live at the head of the map. 
	 */
	private final Map<String, ProvidedResponse> responses = new LinkedHashMap<String, ProvidedResponse>(); 
	private final Object responseLock = new Object();
	
	private final Map<String, DynamicScript> dynamicScripts = new HashMap<String, DynamicScript>(); 
	private final Object dynamicScriptLock = new Object();

	public ResponseProviderImpl() {
	}
	
	@Override
	public byte[] getResponse(String requestId) {
		synchronized (responseLock) {
			ProvidedResponse providedResponse = responses.get(requestId);
			return providedResponse == null ? null : providedResponse.getContent();
		}
	}

	@Override
	public void provideResponse(String requestId, byte[] response) {
		synchronized (responseLock) {
			responses.put(requestId, new ProvidedResponse(new Date(), response));
		}
	}
	
	@Override
	public void cleanup(Date ifOlderThan) {
		synchronized (responseLock) {
			Iterator<Entry<String, ProvidedResponse>> it = responses.entrySet().iterator();
			while (it.hasNext()) {
				ProvidedResponse response = it.next().getValue();
				if (ifOlderThan.after(response.getTimestamp())) {
					it.remove();
				}
			}
		}		
	}

	@Override
	public void provideResponseScript(String requestId, List<SimulationStep> steps) {
		synchronized (dynamicScriptLock) {
			dynamicScripts.put(requestId, new DynamicScript(steps));
		}
	}

	@Override
	public List<SimulationStep> getResponseScript(String requestId) {
		synchronized (dynamicScriptLock) {
			DynamicScript script = dynamicScripts.get(requestId);
			return script == null ? Collections.<SimulationStep>emptyList() : script.getSteps();
		}
	}

	@Override
	public int removeResponseScripts(String requestIdPrefix) {
		int removeCount = 0;
		synchronized (dynamicScriptLock) {
			Iterator<Entry<String, DynamicScript>> it = dynamicScripts.entrySet().iterator();
			while (it.hasNext()) {
				if (it.next().getKey().startsWith(requestIdPrefix)) {
					it.remove();
					removeCount++;
				}
			}
		}
		return removeCount;
	}
	
	private static class ProvidedResponse {
		private final Date timestamp;
		private final byte[] content;
		
		public ProvidedResponse(Date added, byte[] content) {
			this.timestamp = added;
			this.content = content;
		}
		
		public Date getTimestamp() {
			return timestamp;
		}
		
		public byte[] getContent() {
			return content;
		}		
	}
	
	private static class DynamicScript {
		private final List<SimulationStep> steps;

		public DynamicScript(List<SimulationStep> steps) {
			this.steps = steps;
		}

		public List<SimulationStep> getSteps() {
			return steps;
		}
	}
}
