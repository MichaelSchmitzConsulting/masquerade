package masquerade.sim.model.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.util.StringUtil;

public class SimulationContextImpl implements SimulationContext {

	private final String requestId;
	private final Map<String, Object> contextVariables;
	private final Converter converter;
	private final FileLoader fileLoader;
	private final NamespaceResolver namespaceResolver;
	private final Object request;
	private final ResponseTrigger responseTrigger;	
	private final ResponseProvider responseProvider;
	private Object content;

	public SimulationContextImpl(String requestId, Object request, ResponseTrigger responseTrigger, Map<String, Object> initialContextVariables, Converter converter, FileLoader fileLoader,
			NamespaceResolver namespaceResolver, ResponseProvider responseProvider) {
		this.requestId = requestId;
		this.request = request;
		this.responseTrigger = responseTrigger;
		this.contextVariables = new HashMap<String, Object>(initialContextVariables);
		this.content = "";
		this.converter = converter;
		this.fileLoader = fileLoader;
		this.namespaceResolver = namespaceResolver;
		this.responseProvider = responseProvider;
	}

	@Override
	public String getRequestId() {
		return requestId;
	}

	@Override
	public <R> R getRequest(Class<R> expectedType) {
		return convert(request, expectedType);
	}

	@Override
	public <R> R getContent(Class<R> expectedContentType) {
		return convert(content, expectedContentType);
	}

	@Override
	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public void setVariable(String name, Object value) {
		contextVariables.put(name, value);
	}

	@Override
	public <T> T getVariable(String name) {
		@SuppressWarnings("unchecked")
		T value = (T) contextVariables.get(name);
		return value;
	}

	@Override
	public boolean hasVariable(String name) {
		return contextVariables.containsKey(name);
	}

	@Override
	public Map<String, Object> getVariables() {
		return new HashMap<String, Object>(contextVariables);
	}

	@Override
	public String substituteVariables(String content) {
		return StringUtil.substituteVariables(contextVariables, content, converter);
	}
	
	@Override
	public NamespaceResolver getNamespaceResolver() {
		return namespaceResolver;
	}

	@Override
	public ResponseProvider getResponseProvider() {
		return responseProvider;
	}

	/**
	 * Loads a file (e.g. a template) of the given name
	 * @param type
	 * @param name
	 * @return {@link InputStream} for the file, or <code>null</code> if not found
	 */
	@Override
	public InputStream load(FileType fileType, String fileName) {
		return fileLoader.load(fileType, fileName);
	}

	@Override
	public <T> T convert(Object value, Class<T> to) {
		return converter.convert(value, to);
	}

	@Override
	public boolean canConvert(Class<?> from, Class<?> to) {
		return converter.canConvert(from, to);
	}

	@Override
	public void sendIntermediateResponse() throws Exception {
		responseTrigger.sendResponse(getContent(Object.class));
	}
}
