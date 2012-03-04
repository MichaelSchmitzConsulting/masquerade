package masquerade.sim.util;

import java.util.List;

import masquerade.sim.model.SimulationStep;

public interface ScriptMarshaller {

	String marshal(List<SimulationStep> steps);
}
