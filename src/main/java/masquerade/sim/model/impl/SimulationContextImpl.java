package masquerade.sim.model.impl;

import masquerade.sim.model.SimulationContext;

public class SimulationContextImpl implements SimulationContext {

	private Object content;
	
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
}
