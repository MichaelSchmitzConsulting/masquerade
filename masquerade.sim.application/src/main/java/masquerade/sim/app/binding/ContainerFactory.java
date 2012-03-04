package masquerade.sim.app.binding;

import com.vaadin.data.Container;

/**
 * Interface for data providers providing a list of data 
 * having a common type in a Vaadin data {@link Container}
 */
public interface ContainerFactory {
	Class<?> getType();
	
	Container createContainer();
}
