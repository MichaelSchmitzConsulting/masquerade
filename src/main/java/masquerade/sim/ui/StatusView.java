package masquerade.sim.ui;

import java.util.Collection;

import masquerade.sim.status.Status;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * A view displaying status messages from the simulation engine:
 * Informations, warnings, errors.
 */
public class StatusView extends VerticalLayout {
	private static final String[] COLUMNS = new String[]{ 
		"timestamp", "severity", "message" 
	};
	
	private Table table;

	public StatusView() {
		buildLayout();
	}

	public void refresh(Collection<Status> statusLog) {
		table.setContainerDataSource(createContainer(statusLog));
		table.setVisibleColumns(COLUMNS);
		table.setColumnExpandRatio("message", 1.0f);
		table.setSortDisabled(false);
		table.setSortContainerPropertyId("timestamp");
		table.setSortAscending(false);
	}
	
	private void buildLayout() {
		table = new Table();
		table.setSizeFull();
		addComponent(table);
		setExpandRatio(table, 1.0f);
	}

	private Container createContainer(Collection<Status> statusLog) {
		return new BeanItemContainer<Status>(Status.class, statusLog);
	}
}
