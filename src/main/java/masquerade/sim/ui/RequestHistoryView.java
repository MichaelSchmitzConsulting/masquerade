package masquerade.sim.ui;

import java.util.List;

import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class RequestHistoryView extends HorizontalLayout {
	
	private Table table;

	public RequestHistoryView() {
		buildLayout();
	}
	
	public void refresh(RequestHistory requestHistory) {
		List<HistoryEntry> initialContent = requestHistory.getLatestRequests(100);
		BeanItemContainer<?> container = new BeanItemContainer<HistoryEntry>(HistoryEntry.class, initialContent);
		
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
