package masquerade.sim.util;

import java.util.LinkedList;
import java.util.List;

import masquerade.sim.model.SimulationStep;

import com.thoughtworks.xstream.XStream;

public class XmlScriptUnmarshaller implements ScriptUnmarshaller {

	private final  XStreamUnmarshallerFactory xstreamFactory;

	public XmlScriptUnmarshaller(XStreamUnmarshallerFactory xstreamFactory) {
		this.xstreamFactory = xstreamFactory;
	}
	
	@Override
	public List<SimulationStep> unmarshal(String content) {
		XStream xstream = xstreamFactory.createXStream();
		xstream.alias("steps", LinkedList.class);
		return cast(content, xstream);
	}

	@SuppressWarnings("unchecked")
	private List<SimulationStep> cast(String content, XStream encoder) {
		return (List<SimulationStep>) encoder.fromXML(content);
	}
}
