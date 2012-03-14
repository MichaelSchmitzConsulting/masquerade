package masquerade.sim.model.repository.impl;

import java.lang.reflect.Constructor;

import masquerade.sim.model.Channel;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.impl.DefaultSimulation;
import masquerade.sim.util.DefaultBeanCopier;

/**
 * Handles bean cloning
 */
public class ModelBeanUtils {

	private static DefaultBeanCopier beanCloner = new DefaultBeanCopier();

	public static Simulation copySimulation(Simulation simulation) {
		Simulation copy = new DefaultSimulation(simulation.getId());
		beanCloner.copyBean(simulation, copy);
        return copy;
	}

	public static Channel copyChannel(Channel channel) {
		Channel copy = createNewChannelInstance(channel.getId(), channel.getClass());
		beanCloner.copyBean(channel, copy);
        return copy;
	}

	private static Channel createNewChannelInstance(String id, Class<? extends Channel> type) {
		Constructor<? extends Channel> constructor;
		try {
			constructor = type.getConstructor(String.class);
			return constructor.newInstance(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to copy channel", e);
		}
	}
}
