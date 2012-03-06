package masquerade.sim.model.ui;

import java.util.Collection;

/**
 * Returns instance types for model object interfaces
 */
public interface InstanceTypeProvider {

	Collection<Class<?>> getInstanceTypes();

}
