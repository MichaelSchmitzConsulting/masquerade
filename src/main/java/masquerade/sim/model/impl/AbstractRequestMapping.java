package masquerade.sim.model.impl;

import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;

/**
 * {@link RequestMapping} base providing common properties
 */
public abstract class AbstractRequestMapping<R> implements RequestMapping<R> {

	private String name;
	private Script script;
	private Class<R> acceptedRequestType;
	
	protected AbstractRequestMapping(String name, Script script, Class<R> acceptedRequestType) {
	    this.name = name;
	    this.script = script;
	    this.acceptedRequestType = acceptedRequestType;
    }

	@Override
	public Class<R> acceptedRequestType() {
		return acceptedRequestType;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return toString();
	}

	@Override
	public final Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}
}
