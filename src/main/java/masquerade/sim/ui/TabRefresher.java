package masquerade.sim.ui;

import com.vaadin.data.Container;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;

/**
 * A {@link RefreshListener} for tabs with a master/detail view. Recreates the container
 * using a {@link ContainerFactory} and sets it as the view's data source.
 */
public class TabRefresher implements RefreshListener {

	private ContainerFactory containerFactory;

	/**
	 * @param containerFactory
	 */
	public TabRefresher(ContainerFactory containerFactory) {
		this.containerFactory = containerFactory;
	}

	@Override
	public void refresh(Component component) {
		if (containerFactory != null) {
			ComponentContainer container = (ComponentContainer) component;
			MasterDetailView view = (MasterDetailView) container.getComponentIterator().next();
			Container dataSource = containerFactory.createContainer();
			view.setDataSource(dataSource, new String[] { "name", "description" }); // TODO: Remove column parameter
		}
	}
}
