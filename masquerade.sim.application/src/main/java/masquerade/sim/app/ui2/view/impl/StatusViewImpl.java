package masquerade.sim.app.ui2.view.impl;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Collection;

import masquerade.sim.app.ui.SourceViewWindow;
import masquerade.sim.app.ui2.view.StatusView;
import masquerade.sim.status.Status;

import org.vaadin.codemirror2.client.ui.CodeMode;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * A view displaying status messages from the simulation engine:
 * Informations, warnings, errors.
 */
@SuppressWarnings("serial")
public class StatusViewImpl extends VerticalLayout implements StatusView {
	private static final String[] COLUMNS = new String[]{ 
		"timestamp", "severity", "message" 
	};
	
	private Table table;

	private Button refreshButton;

	private Button clearButton;

	private StatusViewCallback callback;

	public StatusViewImpl() {
		setCaption("Event Log");
		buildLayout();
	}

	@Override
	public void refresh(Collection<Status> statusLog) {
		table.setContainerDataSource(createContainer(statusLog));
		table.setVisibleColumns(COLUMNS);
		table.setColumnExpandRatio("message", 1.0f);
	}
	
	public void addClearListener(ClickListener clickListener) {
		clearButton.addListener(clickListener);
	}
	
	public void bind(StatusViewCallback callback) {
		this.callback = callback;
	}

	private void buildLayout() {
		setSizeFull();
		setMargin(true);
		
		// Table
		table = new Table();
		table.setSizeFull();
		table.setSelectable(true);
		table.setMultiSelect(false);
		addComponent(table);
		setExpandRatio(table, 1.0f);
		
		table.addListener(new ItemClickListener() {
			@Override public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					Status item = (Status) event.getItemId();
					if  (item != null) {
						showItemDetails(item);
					}
				}
			}
		});
		
		// Button layout
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(true, false, false, false);
		buttonLayout.setSpacing(true);
		refreshButton = new Button("Refresh");
		buttonLayout.addComponent(refreshButton);
		refreshButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				callback.onRefresh();
			}
		});
		
		clearButton = new Button("Clear");
		buttonLayout.addComponent(clearButton);
		addComponent(buttonLayout);
		clearButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				callback.onClear();
			}
		});
	}

	private Container createContainer(Collection<Status> statusLog) {
		BeanItemContainer<Status> container = new BeanItemContainer<Status>(Status.class, statusLog);
		container.sort(new String[]{ "time" }, new boolean[]{ false });
		return container;
	}

	private void showItemDetails(Status item) {
		String trace = item.getStacktrace();
		String details;
		if (isEmpty(trace)) {
			details = item.getTimestamp() + " " + item.getMessage();
		} else {
			details = trace;
		}
		
		SourceViewWindow.showModal(getWindow(), "Status Entry Details", details, CodeMode.TEXT);
	}
}
