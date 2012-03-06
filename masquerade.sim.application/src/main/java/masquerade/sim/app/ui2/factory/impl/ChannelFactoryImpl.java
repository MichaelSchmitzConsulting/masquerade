package masquerade.sim.app.ui2.factory.impl;

import masquerade.sim.app.ui2.factory.ChannelFactory;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ModelInstanceTypeProvider;
import masquerade.sim.model.listener.CreateApprover;
import masquerade.sim.model.listener.CreateListener;
import masquerade.sim.model.repository.ModelRepository;
import masquerade.sim.model.ui.CreateObjectDialog;
import masquerade.sim.plugin.PluginRegistry;

import com.vaadin.ui.Window;

/**
 * Shows a model dialog for the user to select a channel implementation and
 * enter an ID for the new channel.
 */
public class ChannelFactoryImpl implements ChannelFactory {

	private final PluginRegistry pluginRegistry;
	private final ModelRepository modelRepository;
	private final Window window;

	public ChannelFactoryImpl(PluginRegistry pluginRegistry, ModelRepository modelRepository, Window window) {
		this.pluginRegistry = pluginRegistry;
		this.modelRepository = modelRepository;
		this.window = window;
	}

	@Override
	public void createChannel(final ChannelFactoryCallback callback) {
		ModelInstanceTypeProvider instanceTypeProvider = new ModelInstanceTypeProvider(Channel.class, pluginRegistry);
		CreateApprover createApprover = new CreateApprover() {
			@Override
			public boolean isNameUsed(Class<?> baseType, String usedName) {
				return modelRepository.getChannelById(usedName) != null;
			}
			@Override
			public boolean canCreate(Class<?> type, String name, StringBuilder errorMsg) {
				if (isNameUsed(Channel.class, name)) {
					errorMsg.append("Channel with ID " + name + " already exists");
					return false;
				} else {
					return true;
				}
			}
		};		
		CreateListener createListener = new CreateListener() {
			@Override
			public void notifyCreate(Object value) {
				Channel channel = (Channel) value;
				modelRepository.insertChannel(channel);
				callback.onCreate(channel);
			}
		};
		
		CreateObjectDialog.showModal(window, "Create Channel", "newChannelId", createListener, createApprover, instanceTypeProvider);
	}

}
