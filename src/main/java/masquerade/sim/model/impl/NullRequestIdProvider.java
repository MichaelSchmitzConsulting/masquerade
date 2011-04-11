package masquerade.sim.model.impl;

public class NullRequestIdProvider extends AbstractRequestIdProvider<Object> {

	public NullRequestIdProvider() {
		super("N/A");
	}

	@Override
	public String getUniqueId(Object request) {
		return null;
	}

	@Override
	public String toString() {
		return "NullRequestIdProvider";
	}
}
