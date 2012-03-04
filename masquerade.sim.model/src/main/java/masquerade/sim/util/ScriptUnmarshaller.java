package masquerade.sim.util;

import java.util.List;

import masquerade.sim.model.SimulationStep;

public interface ScriptUnmarshaller {

	List<SimulationStep> unmarshal(String content);

}
