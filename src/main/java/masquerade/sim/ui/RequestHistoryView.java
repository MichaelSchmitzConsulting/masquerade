package masquerade.sim.ui;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

public class RequestHistoryView extends HorizontalLayout {
	
	private Table table;
	
	public RequestHistoryView() {
		buildLayout();
	}
	
	public void refresh(Container container) {
        table.setContainerDataSource(container);
        Set<Object> visCols = new LinkedHashSet<Object>(Arrays.asList(table.getVisibleColumns()));
        visCols.remove("time");
        table.setVisibleColumns(visCols.toArray());
	}
	
	public void addItemClickListener(ItemClickListener listener) {
		table.addListener(listener);
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
