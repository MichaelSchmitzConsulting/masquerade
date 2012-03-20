package masquerade.sim.model.impl.step;

import masquerade.sim.model.Converter;
import masquerade.sim.model.SimulationContext;
import masquerade.sim.util.StringUtil;

public abstract class AbstractSubstitutingStep extends AbstractSimulationStep {

	private boolean substituteVariables = true;

	public AbstractSubstitutingStep(String name) {
		super(name);
	}

	public AbstractSubstitutingStep() {
	}

	/**
	 * @return <code>true</code> if variables from the context should be susbstitued when loading the template
	 */
	public boolean getSubstituteVariables() {
		return substituteVariables;
	}

	/**
	 * @param substituteVariables If variables from the context should be susbstitued when loading the template
	 */
	public void setSubstituteVariables(boolean substituteVariables) {
		this.substituteVariables = substituteVariables;
	}
	
	protected String substituteVariables(String content, SimulationContext context) {
		if (substituteVariables) {
			Converter converter = context;
			content = StringUtil.substituteVariables(context.getVariables(), content, converter);
		}
		return content;		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (substituteVariables ? 1231 : 1237);
		return result;
	}
}
