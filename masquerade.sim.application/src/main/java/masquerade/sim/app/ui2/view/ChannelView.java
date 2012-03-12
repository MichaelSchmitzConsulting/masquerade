package masquerade.sim.app.ui2.view;

import java.util.List;

import masquerade.sim.model.Channel;

/**
 * Interface for the view allowing to view and edit {@link Channel}s.
 */
public interface ChannelView {
	void setChannelList(List<ChannelInfo> channels);
	void setDetailEditorBean(Object bean);
	
	interface ChannelViewCallback {
		void onSelection(ChannelInfo selection);
		void onRemove(ChannelInfo selection);
		void onAdd();
		void onSave(Channel channel, boolean isPersistent);
		void onRefresh();
	}
}
