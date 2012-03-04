package masquerade.sim.app;

import masquerade.sim.model.Channel;

public interface SendTestRequestAction {
	String onSendTestRequest(Channel target, Object content) throws Exception;
}
