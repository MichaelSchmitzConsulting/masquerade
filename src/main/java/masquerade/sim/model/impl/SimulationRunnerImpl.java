package masquerade.sim.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import masquerade.sim.history.RequestHistory;
import masquerade.sim.history.RequestHistoryFactory;
import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.model.SimulationRunner;

import org.apache.commons.io.IOUtils;

/**
 * Default implementation of {@link SimulationRunner}. Applies a {@link RequestMapping} to an incoming request,
 * logs the request, extracts a request ID and returns the response.
 */
public class SimulationRunnerImpl implements SimulationRunner {

	private RequestHistoryFactory requestHistoryFactory;
	private Converter converter;
	private FileLoader fileLoader;

	public SimulationRunnerImpl(RequestHistoryFactory requestHistoryFactory, Converter converter, FileLoader fileLoader) {
		this.requestHistoryFactory = requestHistoryFactory;
		this.converter = converter;
		this.fileLoader = fileLoader;
	}

	@Override
	public void runSimulation(OutputStream responseOutput, String channelName, String clientInfo, Collection<RequestMapping<?>> requestMappings, Object request) throws Exception {

		RequestHistory requestHistory = requestHistoryFactory.startRequestHistorySession();
		
		try {
			for (RequestMapping<?> mapping : requestMappings) {
				if (mapping.accepts(request.getClass()) && matches(mapping, request)) {
					ResponseSimulation simulation = mapping.getResponseSimulation();
					
					String requestId = getRequestId(simulation.getRequestIdProvider(), request);
					requestHistory.logRequest(channelName, simulation.getName(), clientInfo, requestId, converter.convert(request, String.class));
					
					Object response = simulation.getScript().run(request, converter, fileLoader);
					marshalResponse(response, responseOutput);
					return;
				}
			}
			
			// No match found
			requestHistory.logRequest(channelName, null, clientInfo, null, converter.convert(request, String.class));
		} finally {
			requestHistory.endSession();
		}
	}

	private void marshalResponse(Object response, OutputStream responseOutput) throws IOException {
		InputStream input = converter.convert(response, InputStream.class);
		if (input == null) {
			byte[] ba = converter.convert(response, byte[].class);
			if (ba == null) {
				String str = converter.convert(response, String.class);
				IOUtils.write(str, responseOutput);
			} else {
				IOUtils.write(ba, responseOutput);
			}
		} else {
			IOUtils.copy(input, responseOutput);
		}
	}

	// Casts are safe because accepts() checks the request type
	private boolean matches(RequestMapping<?> mapping, Object request) {
		@SuppressWarnings("rawtypes")
		RequestMapping cast = (RequestMapping) mapping;
		@SuppressWarnings("unchecked")
		boolean matches = cast.matches(request);
		return matches;
	}
 
	// Casts are safe because ResponseSimulation.matches() is typed
	private String getRequestId(RequestIdProvider<?> requestIdProvider, Object request) {
		if (requestIdProvider == null) {
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		RequestIdProvider cast = requestIdProvider;
		@SuppressWarnings("unchecked")
		String uniqueId = cast.getUniqueId(request);
		return uniqueId;
	}
}
