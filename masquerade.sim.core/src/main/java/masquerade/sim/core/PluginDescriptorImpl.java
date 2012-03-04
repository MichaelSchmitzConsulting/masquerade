package masquerade.sim.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.util.DomUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Default implementation of  PluginDescriptor, manages plugins by storing
 * a list of plugins in an XML plugin descriptor file.
 */
public class PluginDescriptorImpl implements PluginDescriptor {
	private static final String PLUGIN_BUNDLE_EXTENSION = ".jar";
	private static final String DESCRIPTOR_FILE_NAME = "plugins.xml";
	
	private final File pluginLocation;
	private final Descriptor descriptor;
	
	/**
	 * @param pluginLocation
	 */
	public PluginDescriptorImpl(File pluginLocation) {
		this.pluginLocation = pluginLocation;
		this.descriptor = new Descriptor(new File(pluginLocation, DESCRIPTOR_FILE_NAME));
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.core.PluginDescriptor#notifyInstalled(java.lang.String)
	 */
	@Override
	public void notifyInstalled(String pluginFileName) {
		descriptor.add(pluginFileName);
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.core.PluginDescriptor#notifyUninstalled(java.lang.String)
	 */
	@Override
	public void notifyUninstalled(String pluginFileName) {
		descriptor.remove(pluginFileName);
	}

	/* (non-Javadoc)
	 * @see masquerade.sim.core.PluginDescriptor#listPlugins()
	 */
	@Override
	public Collection<File> listPlugins() {
		return descriptor.list();
	}

	private Collection<File> listPluginDirectory() {
		File[] files = pluginLocation.listFiles(new FilenameFilter() {
			@Override public boolean accept(File file, String name) {
				return name.endsWith(PLUGIN_BUNDLE_EXTENSION);
			}
		});
		
		return Arrays.asList(files);
	}
	
	/**
	 * Wraps the XML serialization of the list of plugins in a descriptor file
	 */
	private final class Descriptor {
		
		private static final String PLUGIN_JAR = "PluginJar";
		private static final String MASQUERADE_PLUGIN_DESCRIPTOR = "PluginDescriptor";
		
		private final File descriptorFile;
		private final Set<String> plugins = new LinkedHashSet<String>();
		
		public Descriptor(File descriptorFile) {
			this.descriptorFile = descriptorFile;
			
			synchronized (plugins) {
				initPlugins();
			}
		}

		public Collection<File> list() {
			synchronized (plugins) {
				Collection<File> files = new ArrayList<File>(plugins.size());
				for (String pluginFileName : plugins) {
					files.add(new File(pluginLocation, pluginFileName));
				}
				return files;
			}
		}

		/**
		 * @param pluginFileName
		 */
		public void remove(String pluginFileName) {
			synchronized (plugins) {
				plugins.remove(pluginFileName);
				writeDescriptor();
			}
		}

		public void add(String pluginFileName) {
			synchronized (plugins) {
				plugins.add(pluginFileName);
				writeDescriptor();
			}
		}

		private void initPlugins() {
			if (descriptorFile.exists()) {
				try {
						Document doc = DomUtil.parse(new FileInputStream(descriptorFile));
						NodeList elements = doc.getElementsByTagName(PLUGIN_JAR);
						for (int i = 0; i < elements.getLength(); ++i) {
							Element el = (Element) elements.item(i);
							String pluginFileName = el.getTextContent();
							plugins.add(pluginFileName);
					}
				} catch (FileNotFoundException e) {
					// Ignore - file existance check is done above. Non-existing descriptors
					// are expected before the first plugin is installed.
				}
			} else {
				// No plugin descriptor exists yet - find all plugins in the plugin directory
				Collection<File> list = listPluginDirectory();
				for (File pluginFile : list) {
					plugins.add(pluginFile.getName());
				}
			}
		}

		private void writeDescriptor() {
			Document doc = DomUtil.createDocument(MASQUERADE_PLUGIN_DESCRIPTOR);
			
			Element rootElement = doc.getDocumentElement();
			for (String pluginFileName : plugins) {
				Element pluginJar = doc.createElement(PLUGIN_JAR);
				Text text = doc.createTextNode(pluginFileName);
				pluginJar.appendChild(text);
				rootElement.appendChild(pluginJar);
			}
			
			try {
				DomUtil.write(doc, new FileOutputStream(descriptorFile));
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("Unable to write plugin descriptor file", e);
			}
		}		
	}
}
