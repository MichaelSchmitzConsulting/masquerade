package masquerade.sim.model;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import masquerade.sim.model.impl.NamespaceResolverImpl;
import masquerade.sim.model.response.ResponseProvider;
import masquerade.sim.util.StringUtil;

/**
 * Stub implementation of {@link SimulationContext} for testing
 */
public class SimulationContextStub implements SimulationContext {

	private final Map<String, Object> variables = new HashMap<String, Object>();
	private final NamespaceResolverImpl nsResolver = new NamespaceResolverImpl();
	private final Object request;
	private Object response;

	public SimulationContextStub(Object request) {
		this(request, null);
	}

	public SimulationContextStub(Object request, Object response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public String getRequestId() {
		return "123";
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object value, Class<T> to) {
		return (T) value;
	}

	@Override
	public boolean canConvert(Class<?> from, Class<?> to) {
		return from == to;
	}

	@Override
	public Map<String, Object> getVariables() {
		return variables;
	}

	@Override
	public String substituteVariables(String content) {
		return StringUtil.substituteVariables(variables, content, this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getRequest(Class<R> expectedType) {
		return (R) request;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <C> C getContent(Class<C> expectedType) {
		return (C) response;
	}

	@Override
	public void setContent(Object content) {
		response = content;
	}

	@Override
	public void setVariable(String name, Object value) {
		variables.put(name, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getVariable(String name) {
		return (T) variables.get(name);
	}

	@Override
	public boolean hasVariable(String name) {
		return variables.containsKey(name);
	}

	@Override
	public NamespaceResolverImpl getNamespaceResolver() {
		return nsResolver;
	}

	@Override
	public InputStream load(FileType fileType, String fileName) {
		throw new UnsupportedOperationException("load");
	}

	@Override
	public ResponseProvider getResponseProvider() {
		return new ResponseProvider() {
			@Override
			public void provideResponse(String requestId, byte[] response) {
			}
			@Override
			public byte[] getResponse(String requestId) {
				return "<test/>".getBytes();
			}
			@Override
			public void cleanup(Date ifOlderThan) { }
			@Override
			public void provideResponseScript(String requestId, List<SimulationStep> steps) {
			}
			@Override
			public int removeResponseScripts(String requestIdPrefix) {
				return 0;
			}
			@Override
			public List<SimulationStep> getResponseScript(String requestId) {
				return Collections.emptyList();
			}
		};
	}

	@Override
	public void sendIntermediateResponse() {
		// nop
	}
	
}
