package masquerade.sim.app.ui2.presenter;

import java.util.ArrayList;
import java.util.List;

import masquerade.sim.app.ui2.factory.ChannelFactory;
import masquerade.sim.app.ui2.factory.ChannelFactory.ChannelFactoryCallback;
import masquerade.sim.app.ui2.view.ChannelView;
import masquerade.sim.app.ui2.view.ChannelView.ChannelInfo;
import masquerade.sim.channellistener.ChannelListenerRegistry;
import masquerade.sim.model.Channel;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.util.ClassUtil;

/**
 * Presenter for {@link ChannelView}
 */
public class ChannelPresenter implements ChannelView.Callback {
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
			Channel channel = modelRepository.getChannelById(selection.getId());
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
		modelRepository.deleteChannel(selection.getId());
		onRefresh();
	}

	@Override
	public void onAdd() {
		channelFactory.createChannel(new ChannelFactoryCallback() {
			@Override
			public void onCreate(Channel channel) {
				channelListenerRegistry.startOrRestart(channel.getName());
				// Refresh channel list after creating a new channel
				onRefresh();
			}
		});
	}

	@Override
	public void onRefresh() {
		List<ChannelInfo> channels = buildChannelInfoList();
		view.setChannelList(channels);
	}

	private List<ChannelInfo> buildChannelInfoList() {
		List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
		for (Channel channel : modelRepository.getChannels()) {
			String type = ClassUtil.fromCamelCase(channel.getClass());
			channels.add(new ChannelInfo(channel.getName(), type));
		}
		return channels;
	}	
}
