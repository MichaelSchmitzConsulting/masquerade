package masquerade.sim.client;

@SuppressWarnings("serial")
public class MasqueradeClientException extends RuntimeException {

	public MasqueradeClientException(String message) {
		super(message);
	}

	public MasqueradeClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
