package masquerade.sim.model.impl.step;

import java.io.IOException;

import masquerade.sim.model.SimulationContext;
import masquerade.sim.model.SimulationStep;

import org.apache.commons.io.IOUtils;

/**
 * A {@link SimulationStep} creating a SOAP fault response
 */
public class SoapFaultStep extends AbstractSubstitutingStep {

	public static final String VAR_SOAP_FAULTCODE = "soap.faultcode";
	public static final String VAR_SOAP_FAULTSTRING = "soap.faultstring";
	public static final String VAR_SOAP_FAULTDETAIL = "soap.faultdetail";
	public final static String DEFAULT_FAULTCODE = "SOAP-ENV:Server";
	public final static String DEFAULT_FAULTSTRING = "Simulated SOAP fault response";
	public static final String DEFAULT_FAULTDETAIL = "";
		
	private final static String faultContent;
	
	static {
		try {
			faultContent = IOUtils.toString(SoapFaultStep.class.getResourceAsStream("SoapFault.template"));
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public SoapFaultStep(String name) {
		super(name);
	}
	
	public SoapFaultStep() {
	}

	/**
	 * Substitutes {@link #VAR_SOAP_FAULTCODE} and {@link #VAR_SOAP_FAULTSTRING} (with default
	 * values if variables not set in context) and returns a SOAP fault message.
	 */
	@Override
	public void execute(SimulationContext context) throws Exception {
		if (!context.hasVariable(VAR_SOAP_FAULTCODE)) {
			context.setVariable(VAR_SOAP_FAULTCODE, DEFAULT_FAULTCODE);
		}
		if (!context.hasVariable(VAR_SOAP_FAULTSTRING)) {
			context.setVariable(VAR_SOAP_FAULTSTRING, DEFAULT_FAULTSTRING);
		}
		if (!context.hasVariable(VAR_SOAP_FAULTDETAIL)) {
			context.setVariable(VAR_SOAP_FAULTDETAIL, DEFAULT_FAULTDETAIL);
		}
		
		String content = substituteVariables(faultContent, context);
		context.setContent(content);
	}
}
