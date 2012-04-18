package masquerade.sim.app.ui.view;

import java.util.Collection;

/**
 * View for sending requests to channels from a text field for testing simuations.
 */
public interface RequestTestView {
	void setChannels(Collection<String> channelIds);
	
	public interface RequestTestViewCallback {
		String onSendTestRequest(String channelId, String content) throws Exception;
	}
}
