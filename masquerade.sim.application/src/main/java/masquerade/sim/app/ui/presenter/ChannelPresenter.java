package masquerade.sim.app.ui.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import masquerade.sim.app.ui.Refreshable;
import masquerade.sim.app.ui.factory.ChannelFactory;
import masquerade.sim.app.ui.factory.ChannelFactory.ChannelFactoryCallback;
import masquerade.sim.app.ui.view.ChannelInfo;
import masquerade.sim.app.ui.view.ChannelView;
import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.repository.ChannelWrapper;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.util.ClassUtil;

/**
 * Presenter for {@link ChannelView}
 */
public class ChannelPresenter implements ChannelView.ChannelViewCallback, Refreshable {
	private final ChannelView view;
	private final ModelRepository modelRepository;
	private final ChannelFactory channelFactory;
	private final ChannelListenerRegistry channelListenerRegistry;

	public ChannelPresenter(ChannelView view, ModelRepository modelRepository, ChannelFactory channelFactory, ChannelListenerRegistry channelListenerRegistry) {
		this.view = view;
		this.modelRepository = modelRepository;
		this.channelFactory = channelFactory;
		this.channelListenerRegistry = channelListenerRegistry;
	}

	@Override
	public void onSelection(ChannelInfo selection) {
		if (selection == null) {
			view.setDetailEditorBean(null);
		} else {
			Channel channel = modelRepository.getChannelForUpdate(selection.getId());
			showChannel(channel);
		}
	}

	private void showChannel(Channel channel) {
		if (channel == null) {
			// Selected channel does no longer exist, view is out of date -> reload
			onRefresh();
		} else {
			view.setDetailEditorBean(channel);				
		}
	}

	@Override
	public void onRemove(ChannelInfo selection) {
		String id = selection.getId();
		channelListenerRegistry.stop(id);
		modelRepository.deleteChannel(id);
		onRefresh();
	}

	@Override
	public void onAdd() {
		channelFactory.createChannel(new ChannelFactoryCallback() {
			@Override
			public void onCreate(Channel channel) {
				modelRepository.insertChannel(channel, true);
				channelListenerRegistry.startOrRestart(channel.getId());
				// Refresh channel list after creating a new channel
				onRefresh();
			}
		});
	}

	@Override
	public void onSave(Channel channel, boolean isPersistent) {
		modelRepository.insertChannel(channel, isPersistent);
		channelListenerRegistry.startOrRestart(channel.getId());
	}

	@Override
	public void onRefresh() {
		List<ChannelInfo> channels = buildChannelInfoList();
		view.setChannelList(channels);
	}

	private List<ChannelInfo> buildChannelInfoList() {
		List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
		for (ChannelWrapper wrapper : modelRepository.listChannels()) {
			Channel channel = wrapper.getChannel();
			String type = ClassUtil.fromCamelCase(channel.getClass());
			channels.add(new ChannelInfo(channel.getId(), type, wrapper.isPersistent()));
		}
		Collections.sort(channels);
		return channels;
	}	
}
