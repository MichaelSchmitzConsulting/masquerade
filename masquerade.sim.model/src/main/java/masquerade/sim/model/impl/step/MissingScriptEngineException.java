package masquerade.sim.model.impl.step;

/**
 * Exception thrown when a required script engine (JSR 223) is missing
 */
public class MissingScriptEngineException extends Exception {
	private static final long serialVersionUID = -399291289713694244L;

	public MissingScriptEngineException(String msg) {
		super(msg);
	}

	public MissingScriptEngineException() {
	}
}
