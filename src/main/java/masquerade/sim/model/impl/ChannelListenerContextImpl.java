package masquerade.sim.model.impl;

import java.util.HashMap;
import java.util.Map;

import masquerade.sim.model.ChannelListenerContext;
import masquerade.sim.model.VariableHolder;

/**
 * Implementation of {@link ChannelListenerContext}. Stores channel listener
 * context attributes in a {@link Map}.
 */
public class ChannelListenerContextImpl implements ChannelListenerContext {

	private Map<String, Object> attributes = new HashMap<String, Object>();
	private VariableHolder variableHolder;
	
	public ChannelListenerContextImpl(VariableHolder variableHolder) {
		this.variableHolder = variableHolder;
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		synchronized (attributes) {
			attributes.put(name, value);
		}
	}

	@Override
	public <T> T getAttribute(String name) {
		synchronized (attributes) {
			@SuppressWarnings("unchecked")
			T value = (T) attributes.get(name);
			return value;
		}
	}

	@Override
	public void removeAttribute(String name) {
		synchronized (attributes) {
			attributes.remove(name);
		}
	}

	@Override
	public VariableHolder getVariableHolder() {
		return variableHolder;
	}
}
