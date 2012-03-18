package masquerade.sim.app.ui2.view.impl;

import java.util.Collection;

import masquerade.sim.app.ui.SourceViewWindow;
import masquerade.sim.app.ui2.view.RequestTestView;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;
import masquerade.sim.util.WindowUtil;

import org.vaadin.codemirror2.CodeMirror;
import org.vaadin.codemirror2.client.ui.CodeMode;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * A view for sending test requests to channel
 */
@SuppressWarnings("serial")
public class RequestTestViewImpl extends VerticalLayout implements RequestTestView {

	protected static final StatusLog log = StatusLogger.get(RequestTestViewImpl.class);
	
	private Select channelSelect;
	private RequestTestViewCallback callback;
	private Button sendButton;
	private CodeMirror requestContent;

	public RequestTestViewImpl() {
		setSpacing(true);
		setMargin(true);
		setCaption("Test");
		
		requestContent = new CodeMirror("Test Request", CodeMode.XML);
		requestContent.setWidth("600px");
		requestContent.setHeight("300px");
		requestContent.setValue("<test/>");
		requestContent.setImmediate(true);
		requestContent.setTextChangeEventMode(TextChangeEventMode.EAGER);
		addComponent(requestContent);
		setExpandRatio(requestContent, 1.0f);
		
		HorizontalLayout bottom = new HorizontalLayout();
		bottom.setSpacing(true);
		
		// Channel label
		bottom.addComponent(new Label("Channel"));
		
		// Target channel selection
		channelSelect = new Select();
		channelSelect.setNullSelectionAllowed(false);
		channelSelect.setImmediate(true);
		channelSelect.setNewItemsAllowed(false);
		bottom.addComponent(channelSelect);
		
		sendButton = new Button("Send");
		bottom.addComponent(sendButton);
		
		addComponent(bottom);
		addSendListener(sendButton);
	}

	private void addSendListener(Button sendButton) {
		sendButton.addListener(new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				try {
					String content = (String) requestContent.getValue();
					String response = callback.onSendTestRequest((String) channelSelect.getValue(), content);
					if (response != null) {
						SourceViewWindow.showModal(getWindow(), "Response", response, CodeMode.XML);
					} else {
						WindowUtil.showErrorNotification(getWindow(), "Error", "No response received");
					}
				} catch (Exception e) {
					log.error("Unable to handle test request", e);
					WindowUtil.showErrorNotification(getWindow(), "Error", "Script Exception: " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Sets the list of available channels to send test requests to
	 * @param all
	 */
	@Override
	public void setChannels(Collection<String> channelIds) {
		Container container = new IndexedContainer(channelIds);
		channelSelect.setContainerDataSource(container);
		
		if (channelIds.size() > 0) {
			channelSelect.setValue(channelIds.iterator().next());
			sendButton.setEnabled(true);
		} else {
			sendButton.setEnabled(false);
		}
	}

	public void bind(RequestTestViewCallback callback) {
		this.callback = callback;
	}
}
