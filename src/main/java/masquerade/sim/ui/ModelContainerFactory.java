package masquerade.sim.ui;

import java.util.Collection;

import masquerade.sim.db.ModelRepository;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;

public class ModelContainerFactory implements ContainerFactory {

	private ModelRepository repo;
	private Class<?> type;

	public ModelContainerFactory(ModelRepository repo, Class<?> type) {
		this.repo = repo;
		this.type = type;
	}
	
	@Override
	public Container createContainer() {
		Collection<?> all = repo.getAll(type);
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Container container = new BeanItemContainer(type, all);
		return container;
	}

	@Override
	public Class<?> getType() {
		return type;
	}
}
