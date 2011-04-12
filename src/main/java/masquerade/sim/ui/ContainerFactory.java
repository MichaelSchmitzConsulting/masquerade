package masquerade.sim.ui;

import com.vaadin.data.Container;

public interface ContainerFactory {
	Class<?> getType();
	
	Container createContainer();
}
