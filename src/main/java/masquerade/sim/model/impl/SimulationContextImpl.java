package masquerade.sim.model.impl;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;
import masquerade.sim.model.NamespaceResolver;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.util.StringUtil;

public class SimulationContextImpl implements SimulationContext {

	private Object content;
	private Map<String, Object> contextVariables = new HashMap<String, Object>();
	private Converter converter;
	private FileLoader fileLoader;
	private NamespaceResolver namespaceResolver;
	private Object request;	

	public SimulationContextImpl(Object request, Converter converter, FileLoader fileLoader, NamespaceResolver namespaceResolver) {
		this.request = request;
		this.content = "";
		this.converter = converter;
		this.fileLoader = fileLoader;
		this.namespaceResolver = namespaceResolver;
	}

	@Override
	public <R> Object getRequest(Class<R> expectedType) {
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
	public Map<String, Object> getVariables() {
		return Collections.unmodifiableMap(contextVariables);
	}

	@Override
	public String substituteVariables(String content) {
		return StringUtil.substituteVariables(contextVariables, content, converter);
	}
	
	@Override
	public NamespaceResolver getNamespaceResolver() {
		return namespaceResolver;
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
}
