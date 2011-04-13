package masquerade.sim.model.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import masquerade.sim.model.Converter;
import masquerade.sim.model.FileLoader;
import masquerade.sim.model.FileType;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.util.StringUtil;

public class SimulationContextImpl implements SimulationContext {

	private Object content;
	private Map<String, Object> contextVariables = new HashMap<String, Object>();
	private Converter converter;
	private FileLoader fileLoader;	

	public SimulationContextImpl(Object content, Converter converter, FileLoader fileLoader) {
		this.content = content;
		this.converter = converter;
		this.fileLoader = fileLoader;
	}

	@Override
	public <R> R getContent(Class<R> expectedContentType) {
		@SuppressWarnings("unchecked")
		R content = (R) this.content;
		return content;
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
	public String substituteVariables(String content) {
		return StringUtil.substituteVariables(contextVariables, content, converter);
	}

	@Override
	public InputStream load(FileType fileType, String fileName) {
		return fileLoader.load(fileType, fileName);
	}

	@Override
	public <T> T convert(Object value, Class<T> to) {
		return converter.convert(value, to);
	}
}
