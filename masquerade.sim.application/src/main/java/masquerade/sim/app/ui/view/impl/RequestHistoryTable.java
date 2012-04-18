package masquerade.sim.app.ui.view.impl;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import masquerade.sim.model.history.HistoryEntry;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * A {@link HorizontalLayout} with a table showing request/response
 * history log informations.
 * @see #refresh(Container)
 * @see #addItemClickListener(ItemClickListener)
 */
@SuppressWarnings("serial")
public class RequestHistoryTable extends VerticalLayout {
	
	private TextField filterField;
	private Table table;
	
	public RequestHistoryTable() {
		buildLayout();
	}
	
	/**
	 * Refreshes the request log contents from a container containing
	 * {@link HistoryEntry} objects.
	 * @param container
	 */
	public void setData(Filterable container) {
		table.setContainerDataSource(container);
        Set<Object> visCols = new LinkedHashSet<Object>(Arrays.asList(table.getVisibleColumns()));
        visCols.remove("requestTime");
        visCols.remove("receiveTime");
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
		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false, false, true, false);
		
		filterField = new TextField();
		filterField.setInputPrompt("Request ID Filter");
		filterField.setImmediate(true);
		filterField.setValue("");
		filterLayout.addComponent(filterField);
		
		Button filterButton = new Button("Filter", new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				doFilter((String) filterField.getValue());
			}
		});
		filterLayout.addComponent(filterButton);
		
		Button clearButton = new Button("Clear", new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				filterField.setValue("");
				doFilter("");
			}
		});
		filterLayout.addComponent(clearButton);
		
        table = new Table(null, null);
        table.setSelectable(true);
        table.setMultiSelect(false);
        table.setImmediate(true);
        table.setNullSelectionAllowed(true);
        table.setSizeFull();
        
        addComponent(filterLayout);
        addComponent(table);
        setExpandRatio(table, 1.0f);
	}

	protected void doFilter(String filterString) {
		Filterable container = (Filterable) table.getContainerDataSource();
		if (filterString.length() > 0) {
			container.addContainerFilter(new SimpleStringFilter("requestId", filterString, true, false));
		} else {
			container.removeAllContainerFilters();
		}
	}
}
