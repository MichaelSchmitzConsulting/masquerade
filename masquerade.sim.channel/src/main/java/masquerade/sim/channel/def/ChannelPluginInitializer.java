package masquerade.sim.channel.def;

import masquerade.sim.channel.http.HttpChannel;
import masquerade.sim.channel.http.HttpStandaloneChannel;
import masquerade.sim.model.Channel;
import masquerade.sim.plugin.PluginRegistry;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;

@Component
public class ChannelPluginInitializer {
	
	@Reference protected PluginRegistry pluginRegistry;
	
	@Activate
	protected void activate(ComponentContext componentContext) {
		pluginRegistry.registerExtension(Channel.class, HttpChannel.class);
		pluginRegistry.registerExtension(Channel.class, HttpStandaloneChannel.class);
	}
}
