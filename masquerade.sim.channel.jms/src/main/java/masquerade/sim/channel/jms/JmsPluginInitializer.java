package masquerade.sim.channel.jms;

import masquerade.sim.channel.jms.impl.ActiveMqConnectionFactoryProvider;
import masquerade.sim.channel.jms.impl.ConnectionFactoryProvider;
import masquerade.sim.channel.jms.impl.TibcoEmsConnectionFactoryProvider;
import masquerade.sim.model.Channel;
import masquerade.sim.model.ui.ModelImplementationSelectFieldFactory;
import masquerade.sim.model.ui.UiConstant;
import masquerade.sim.plugin.PluginRegistry;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;

/**
 * Plugin initializer for the Masquerade JMS plugin
 */
@Component
public class JmsPluginInitializer {
	
	@Reference protected PluginRegistry pluginRegistry;
	
	@Activate
	protected void activate(ComponentContext componentContext) {
		registerExtensions();
		registerPropertyEditors();
	}

	private void registerPropertyEditors() {
		pluginRegistry.registerPropertyEditor(
				DefaultJmsChannel.class,
				"connectionFactoryProvider",
				new ModelImplementationSelectFieldFactory(ConnectionFactoryProvider.class, UiConstant.DEFAULT_WIDTH, pluginRegistry));
	}

	private void registerExtensions() {
		// Channels
		pluginRegistry.registerExtension(Channel.class, DefaultJmsChannel.class);
		pluginRegistry.registerExtension(Channel.class, WebSphereMqJmsChannel.class);
		
		// JMS Connection Factory Providers
		pluginRegistry.registerExtension(ConnectionFactoryProvider.class, ActiveMqConnectionFactoryProvider.class);
		pluginRegistry.registerExtension(ConnectionFactoryProvider.class, TibcoEmsConnectionFactoryProvider.class);
	}
}
