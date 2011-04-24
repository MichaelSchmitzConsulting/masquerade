package masquerade.sim.ui;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.history.HistoryEntry;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;

/**
 * A {@link HorizontalLayout} with a table showing request/response
 * history log informations.
 * @see #refresh(Container)
 * @see #addItemClickListener(ItemClickListener)
 */
public class RequestHistoryView extends HorizontalLayout {
	
	private Table table;
	
	public RequestHistoryView() {
		buildLayout();
	}
	
	/**
	 * Refreshes the request log contents from a container containing
	 * {@link HistoryEntry} objects.
	 * @param container
	 */
	public void refresh(Container container) {
        table.setContainerDataSource(container);
        Set<Object> visCols = new LinkedHashSet<Object>(Arrays.asList(table.getVisibleColumns()));
        visCols.remove("time");
        visCols.remove("fileName");
        table.setVisibleColumns(visCols.toArray());
	}
	
	/**
	 * @param listener Listener handling clicks on the request log table
	 */
	public void addItemClickListener(ItemClickListener listener) {
		table.addListener(listener);
	}
	
	/**
	 * @param listener Listener hanlding value change (selection) events on the request log table
	 */
	public void addValueChangeListener(ValueChangeListener listener) {
		table.addListener(listener);
	}
	
	public HistoryEntry getSelection() {
		return (HistoryEntry) table.getValue();
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
