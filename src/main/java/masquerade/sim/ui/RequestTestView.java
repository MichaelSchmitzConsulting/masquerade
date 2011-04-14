package masquerade.sim.ui;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import masquerade.sim.model.Channel;
import masquerade.sim.util.DomUtil;
import masquerade.sim.util.WindowUtil;

import org.vaadin.codemirror.CodeMirror;
import org.vaadin.codemirror.client.ui.CodeStyle;
import org.w3c.dom.Document;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
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
public class RequestTestView extends VerticalLayout {

	protected static final Logger log = Logger.getLogger(RequestTestView.class.getName());
	
	private Select channelSelect;
	private ActionListener<Channel, String, Object> sendActionListener;
	private Button sendButton;
	private CodeMirror requestContent;

	public RequestTestView(ActionListener<Channel, String, Object> sendActionListener) {
		this.sendActionListener = sendActionListener;
		
		setSpacing(true);
		
		requestContent = new CodeMirror("Test Request", CodeStyle.XML);
		requestContent.setWidth("600px");
		requestContent.setHeight("300px");
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
					Document content = DomUtil.parse((String) requestContent.getValue()); // TODO: Type select in UI (String/Document)
					String response = sendActionListener.onAction((Channel) channelSelect.getValue(), content);
					if (response != null) {
						SourceViewWindow.showModal(getWindow(), "Response", response, CodeStyle.XML);
					} else {
						WindowUtil.showErrorNotification(getWindow(), "Error", "No response received");
					}
				} catch (Exception e) {
					log.log(Level.SEVERE, "Unable to handle test request", e);
					WindowUtil.showErrorNotification(getWindow(), "Error", "Script Exception: " + e.getMessage());
				}
			}
		});
	}
	
	/**
	 * Sets the list of available channels to send test requests to
	 * @param all
	 */
	public void setChannels(Collection<Channel> all) {
		Container container = new BeanItemContainer<Channel>(Channel.class, all);
		channelSelect.setContainerDataSource(container);
		
		if (all.size() > 0) {
			channelSelect.setValue(all.iterator().next());
		} else {
			sendButton.setEnabled(false);
		}
	}
}
