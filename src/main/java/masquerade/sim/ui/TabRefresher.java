package masquerade.sim.ui;

import com.vaadin.data.Container;
import com.vaadin.ui.ComponentContainer;

/**
 * A {@link RefreshListener} for tabs with a master/detail view. Recreates the container
 * using a {@link ContainerFactory} and sets it as the view's data source.
 */
public class TabRefresher implements RefreshListener {

	private ContainerFactory containerFactory;
	private ComponentContainer tabLayout;
	
	/**
	 * @param containerFactory
	 */
	public TabRefresher(ContainerFactory containerFactory, ComponentContainer tabLayout) {
		this.containerFactory = containerFactory;
		this.tabLayout = tabLayout;
	}

	@Override
	public void refresh() {
		if (containerFactory != null) {
			MasterDetailView view = (MasterDetailView) tabLayout.getComponentIterator().next();
			Container dataSource = containerFactory.createContainer();
			view.setDataSource(dataSource);
		}
	}
}
