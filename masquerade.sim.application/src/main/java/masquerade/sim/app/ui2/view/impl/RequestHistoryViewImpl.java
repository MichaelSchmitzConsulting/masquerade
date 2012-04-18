package masquerade.sim.app.ui2.view.impl;

import org.vaadin.codemirror2.client.ui.CodeMode;

import masquerade.sim.app.ui2.dialog.view.impl.SourceViewWindow;
import masquerade.sim.app.ui2.view.RequestHistoryView;
import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class RequestHistoryViewImpl extends VerticalLayout implements RequestHistoryView {
	private static final boolean RESPONSE = false;
	private static final boolean REQUEST = true;

	private RequestHistoryViewCallback callback;
	private RequestHistoryTable requestHistoryTable;

	public RequestHistoryViewImpl() {
		buildLayout();
	}
	
	private void buildLayout() {
		setCaption("History");
		setSizeFull();

		requestHistoryTable = new RequestHistoryTable();
		requestHistoryTable.setMargin(true);
		requestHistoryTable.setSizeFull();
		addComponent(requestHistoryTable);
		setExpandRatio(requestHistoryTable, 1.0f);

		// Show request details on double click
		requestHistoryTable.addItemClickListener(new ItemClickListener() {
			@Override public void itemClick(ItemClickEvent event) {
				if (event.isDoubleClick()) {
					showHistoryContent(((HistoryEntry) event.getItemId()), REQUEST);
				}
			}
		});

		// Button layout container
		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setMargin(false, true, true, true);
		bottomLayout.setSpacing(true);
		bottomLayout.setWidth("100%");
		bottomLayout.setSpacing(true);
		
		// Refresh button
		Button refreshButton = new Button("Refresh");
		refreshButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				callback.onRefresh();
			}
		});
		bottomLayout.addComponent(refreshButton);

		// Clear button
		Button clearButton = new Button("Clear");
		clearButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				callback.onClear();
			}
		});
		bottomLayout.addComponent(clearButton);

		// Spacer
		Label spacer = new Label("Double-click on history entry to show details");
		bottomLayout.addComponent(spacer);
		bottomLayout.setExpandRatio(spacer, 1.0f);

		// Show request button
		final Button requestButton = new Button("Show Request");
		requestButton.setEnabled(false);
		requestButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				HistoryEntry entry = requestHistoryTable.getSelection();
				showHistoryContent(entry, REQUEST);
			}
		});
		bottomLayout.addComponent(requestButton);

		// Show response button
		final Button responseButton = new Button("Show Response");
		responseButton.setEnabled(false);
		responseButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				HistoryEntry entry = requestHistoryTable.getSelection();
				showHistoryContent(entry, RESPONSE);
			}
		});
		bottomLayout.addComponent(responseButton);
		
		// Enable/disable buttons upon selection
		requestHistoryTable.addValueChangeListener(new ValueChangeListener() {
			@Override public void valueChange(ValueChangeEvent event) {
				boolean enabled = event.getProperty().getValue() != null;
				responseButton.setEnabled(enabled);
				requestButton.setEnabled(enabled);
			}
		});
		
		addComponent(bottomLayout);
	}
	
	@Override
	public void setData(Filterable container) {
		requestHistoryTable.setData(container);
	}

	public void bind(RequestHistoryViewCallback callback) {
		this.callback = callback;
	}

	private void showHistoryContent(HistoryEntry historyEntry, boolean isRequest) {
		if (historyEntry != null) {
			callback.onShowEntry(historyEntry, isRequest);
		}
	}

	@Override
	public void showError(String msg) {
		WindowUtil.showErrorNotification(getWindow(), "Error retrieving content", msg);
	}

	@Override
	public void showHistoryEntry(String title, String content) {
		SourceViewWindow.showModal(getWindow(), title, content, CodeMode.XML);
	}
}
