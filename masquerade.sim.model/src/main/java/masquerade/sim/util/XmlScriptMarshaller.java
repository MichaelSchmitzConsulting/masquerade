package masquerade.sim.util;

import java.util.List;

import masquerade.sim.model.SimulationStep;

import com.thoughtworks.xstream.XStream;

/**
 * Marshals/unmarshals a {@link List} of {@link SimulationStep steps}
 * to/from XML
 */
public class XmlScriptMarshaller implements ScriptMarshaller {

	private final XStreamMarshallerFactory xstreamFactory = new XStreamMarshallerFactory();
	
	@Override
	public String marshal(List<SimulationStep> steps) {
		XStream xstream = xstreamFactory.createXStream();
		xstream.alias("steps", steps.getClass());
		return xstream.toXML(steps);
	}

}
