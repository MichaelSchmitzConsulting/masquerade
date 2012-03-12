package masquerade.sim.core.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import masquerade.sim.model.Settings;
import masquerade.sim.model.repository.ModelPersistenceService;
import masquerade.sim.model.repository.SimulationModel;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.util.XStreamMarshallerFactory;
import masquerade.sim.util.XStreamUnmarshallerFactory;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;

/**
 * {@link ModelPersistenceService} implementation serializing model objects using XStream.
 */
public class XmlModelPersistence implements ModelPersistenceService {

	private final File modelPersistenceFile;
	private final File settingsPersistenceFile;
	private final XStreamMarshallerFactory marshallerFactory;
	private final XStreamUnmarshallerFactory unmarshallerFactory;
	
	public XmlModelPersistence(File modelPersistenceFile, File settingsPersistenceFile, PluginRegistry pluginRegistry) {
		this.modelPersistenceFile = modelPersistenceFile;
		this.settingsPersistenceFile = settingsPersistenceFile;
		
		this.marshallerFactory = new XStreamMarshallerFactory();
		this.unmarshallerFactory = new XStreamUnmarshallerFactory(pluginRegistry);
	}

	@Override
	public void persistModel(SimulationModel model) {
		XStream xstream = marshallerFactory.createXStream();
		marshal(model, xstream, modelPersistenceFile);
	}

	@Override
	public SimulationModel loadModel() {
		if (!modelPersistenceFile.exists()) {
			return null;
		}
		return unmarshal(modelPersistenceFile);		
	}

	@Override
	public void persistSettings(Settings settings) {
		XStream xstream = marshallerFactory.createXStream();
		marshal(settings, xstream, settingsPersistenceFile);
	}

	@Override
	public Settings loadSettings() {
		if (!settingsPersistenceFile.exists()) {
			return null;
		}
		return unmarshal(settingsPersistenceFile);
	}
	
	private static void marshal(Object object, XStream xstream, File file) {
		Writer out = null;
		try {
			if (!file.exists() && !file.createNewFile()) {
				throw new IllegalArgumentException("Unable to create model persistence file at " + file.getAbsolutePath());
			}
			
			if (!file.canWrite()) {
				throw new IllegalArgumentException("Unable to write to model persistence file at " + file.getAbsolutePath());
			}
			
			out = new FileWriter(file);
			xstream.toXML(object, out);
		} catch (IOException ex) {
			throw new IllegalArgumentException("Model persistence file ");
		} finally {
			if (out != null) {
				IOUtils.closeQuietly(out);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T unmarshal(File file) {
		return (T) unmarshallerFactory.createXStream().fromXML(file);
	}
}
