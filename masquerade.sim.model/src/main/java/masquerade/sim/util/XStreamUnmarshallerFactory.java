package masquerade.sim.util;

import masquerade.sim.plugin.PluginRegistry;

import com.thoughtworks.xstream.XStream;

/**
 * Creates pre-configured {@link XStream} instances 
 */
public class XStreamUnmarshallerFactory {
	private final XStreamMarshallerFactory xstreamMarshallerFactory = new XStreamMarshallerFactory();
	private final PluginRegistry pluginRegistry;
	
	public XStreamUnmarshallerFactory(PluginRegistry pluginRegistry) {
		this.pluginRegistry = pluginRegistry;
	}

	
	public XStream createXStream() {
		XStream xstream = xstreamMarshallerFactory.createXStream();

		ClassLoader classLoader = pluginRegistry.createExtensionClassLoader();
		xstream.setClassLoader(classLoader);
		
		return xstream;
	}
}
