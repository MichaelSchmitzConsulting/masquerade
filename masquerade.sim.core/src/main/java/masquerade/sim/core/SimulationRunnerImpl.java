package masquerade.sim.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.RequestContext;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseCallback;
import masquerade.sim.model.ResponseDestination;
import masquerade.sim.model.Script;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationRunner;
import masquerade.sim.model.VariableHolder;
import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;
import masquerade.sim.model.impl.RequestContextImpl;
import masquerade.sim.model.impl.ResponseTrigger;
import masquerade.sim.model.impl.SimulationContextImpl;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.commons.io.IOUtils;

/**
 * Default implementation of {@link SimulationRunner}. Applies a {@link RequestMapping} to an incoming request,
 * logs the request, extracts a request ID and returns the response.
 */
public class SimulationRunnerImpl implements SimulationRunner {

	private static final StatusLog log = StatusLogger.get(SimulationRunnerImpl.class);
	
	private final RequestHistory requestHistory;
	private final Converter converter;
	private final FileLoader fileLoader;
	private final VariableHolder configurationVariableHolder;
	private final ResponseProvider responseProvider;
	private final ModelRepository modelRepository;

	/**
	 * @param requestHistoryFactory
	 * @param converter
	 * @param fileLoader
	 * @param namespaceResolver
	 */
	public SimulationRunnerImpl(ModelRepository modelRepository, RequestHistory requestHistory, Converter converter, FileLoader fileLoader, 
			VariableHolder configurationVariableHolder, ResponseProvider responseProvider) {
		this.modelRepository = modelRepository;
		this.requestHistory = requestHistory;
		this.converter = converter;
		this.fileLoader = fileLoader;
		this.configurationVariableHolder = configurationVariableHolder;
		this.responseProvider = responseProvider;
	}

	@Override
	public void runSimulation(ResponseDestination responseDestination, String channelId, String clientInfo, Object request, Date requestTimestamp) throws Exception {
		Date receiveTimestamp = new Date();
		
		Collection<Simulation> simulations = modelRepository.getSimulationsForChannel(channelId);
		
		for (Simulation simulation : simulations) {
			NamespaceResolver namespaceResolver = simulation.getNamespaceResolver();
			RequestContext requestContext = new RequestContextImpl(namespaceResolver, converter);
			if (matches(request, requestContext, simulation)) {
				Script script = simulation.getScript();

				ResponseTrigger responseTrigger = createResponseTrigger(responseDestination);
				
				// Determine request ID if any
				String requestId = getRequestId(simulation.getRequestIdProvider(), request, requestContext);
				
				// Build context
				Map<String, Object> initialContextVariables = configurationVariableHolder.getVariables();
				SimulationContext context = new SimulationContextImpl(requestId, request, responseTrigger, initialContextVariables, converter, fileLoader, namespaceResolver, responseProvider);

				// Log request
				HistoryEntry entry = logRequest(requestTimestamp, receiveTimestamp, channelId, clientInfo, request, requestHistory, simulation.getId(), requestId);

				// Run simulation script
				Object response = runScript(responseDestination, script, context, entry);

				// Log response
				logResponse(requestHistory, entry, response, receiveTimestamp);

				return;
			}
		}

		// No match found
		requestHistory.logNoMatch(requestTimestamp, receiveTimestamp, channelId, clientInfo, converter.convert(request, String.class));
	}

	private Object runScript(final ResponseDestination responseDestination, Script script, SimulationContext context, HistoryEntry entry) throws Exception, IOException {
		try {
			// Run script
			Object response = script.run(context);
	
			// Marshal and send response
			marshalResponse(response, responseDestination.getResponseOutputStream());
			
			return response;
		} catch (Exception ex) {
			// Affix error to request history entry (stack trace is logged in status log by caller)
			entry.setError(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			
			throw ex;
		}
	}

	private ResponseTrigger createResponseTrigger(final ResponseDestination responseDestination) {
		ResponseTrigger responseTrigger = new CallbackResponseTrigger(responseDestination);
		return responseTrigger;
	}

	private HistoryEntry logRequest(Date requestTimestamp, Date receiveTimestamp, String channelName, String clientInfo, Object request, RequestHistory requestHistory, String simulationId, String requestId) {
		HistoryEntry entry = 
			requestHistory.logRequest(requestTimestamp, receiveTimestamp, channelName, simulationId, clientInfo, requestId, converter.convert(request, String.class));
		return entry;
	}

	private void logResponse(RequestHistory requestHistory, HistoryEntry entry, Object response, Date receiveTimestamp) {
		long processingPeriod = System.currentTimeMillis() - receiveTimestamp.getTime();
		String responseData = converter.convert(response, String.class);
		if (responseData != null) {
			requestHistory.setSuccess(responseData, processingPeriod, entry);
		}
	}

	private boolean matches(Object request, RequestContext requestContext, Simulation simulation) {
		try {
			return accepts(simulation, request.getClass()) && matches(simulation.getSelector(), request, requestContext);
		} catch (Exception e) {
			log.error("Exception during Request Mapping evaluation", e);
			return false;
		}
	}

	private boolean accepts(Simulation simulation, Class<? extends Object> requestType) {
		return converter.canConvert(requestType, simulation.getSelector().acceptedRequestType());
	}

	private void marshalResponse(Object response, OutputStream responseOutput) throws IOException {
		if (response == null) {
			return;
		}
		
		InputStream input = converter.convert(response, InputStream.class);
		if (input == null) {
			byte[] ba = converter.convert(response, byte[].class);
			if (ba == null) {
				String str = converter.convert(response, String.class);
				if (str != null) {
					IOUtils.write(str, responseOutput);
				} else {
					log.error("Unable to convert response to InputStream, byte[] or String: " + response.getClass().getName());
				}
			} else {
				IOUtils.write(ba, responseOutput);
			}
		} else {
			IOUtils.copy(input, responseOutput);
		}
	}

	// Casts are safe because acceptedRequestType() returns the request type
	private boolean matches(RequestMapping<?> mapping, Object request, RequestContext requestContext) {
		@SuppressWarnings("rawtypes")
		RequestMapping cast = (RequestMapping) mapping;
		
		Object converted = converter.convert(request, mapping.acceptedRequestType());
		
		@SuppressWarnings("unchecked")
		boolean matches = cast.matches(converted, requestContext);
		return matches;
	}
 
	// Casts are safe because request is converted to RequestIdProvider#getAcceptedRequestType()
	private String getRequestId(RequestIdProvider<?> requestIdProvider, Object request, RequestContext context) {
		if (requestIdProvider == null) {
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		RequestIdProvider cast = requestIdProvider;
		
		Object converted = converter.convert(request, requestIdProvider.getAcceptedRequestType());
		
		@SuppressWarnings("unchecked")
		String uniqueId = cast.getUniqueId(converted, context);
		return uniqueId;
	}
	
	private final class CallbackResponseTrigger implements ResponseTrigger {
		private final ResponseDestination responseDestination;

		/**
		 * @param responseDestination
		 */
		private CallbackResponseTrigger(ResponseDestination responseDestination) {
			this.responseDestination = responseDestination;
		}

		@Override
		public void sendResponse(final Object content) throws Exception {
			ResponseCallback callback = new ResponseCallback() {
				@Override
				public void withResponse(OutputStream responseContent) throws IOException {
					marshalResponse(content, responseContent);
				}
			};
			
			responseDestination.sendIntermediateResponse(callback); 
		}
	}
}
