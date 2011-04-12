package masquerade.sim.ui;

import com.vaadin.data.Container;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class RequestHistoryView extends HorizontalLayout {
	
	private Table table;

	public RequestHistoryView() {
		buildLayout();
	}
	
	public void refresh(Container container) {
        table.setContainerDataSource(container);
	}
	
	private void buildLayout() {
        table = new Table(null, null);
        table.setSelectable(true);
        table.setImmediate(true);
        table.setNullSelectionAllowed(true);
        table.setSizeFull();
        
        addComponent(table);
        setExpandRatio(table, 1.0f);
	}
}
