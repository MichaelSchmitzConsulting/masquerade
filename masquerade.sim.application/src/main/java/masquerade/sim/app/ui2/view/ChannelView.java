package masquerade.sim.app.ui2.view;

import java.util.List;

import masquerade.sim.model.Channel;

/**
 * Interface for the view allowing to view and edit {@link Channel}s.
 */
public interface ChannelView {
	void setChannelList(List<ChannelInfo> channels);
	void setDetailEditorBean(Object bean);
	
	interface Callback {
		void onSelection(ChannelInfo selection);
		void onRemove(ChannelInfo selection);
		void onAdd();
		void onRefresh();
	}
	
	class ChannelInfo {
		private final String id;
		private final String  type;
		public ChannelInfo(String id, String type) {
			this.id = id;
			this.type = type;
		}
		public String getId() {
			return id;
		}
		public String getType() {
			return type;
		}
	}
}
