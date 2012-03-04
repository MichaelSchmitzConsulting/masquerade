package masquerade.sim.util;

import masquerade.sim.model.SimulationStep;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.plugin.impl.ExtensionClassLoader;

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

		ClassLoader modelClassLoader = SimulationStep.class.getClassLoader();
		ExtensionClassLoader classLoader = new ExtensionClassLoader(pluginRegistry, modelClassLoader);
		xstream.setClassLoader(classLoader);
		
		return xstream;
	}
}
