package masquerade.sim.ui;

import java.util.Collection;

import masquerade.sim.model.Channel;

import org.vaadin.codemirror.CodeMirror;
import org.vaadin.codemirror.client.ui.CodeStyle;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;

/**
 * A view for sending test requests to channel
 */
public class RequestTestView extends VerticalLayout {

	private Select channelSelect;

	public RequestTestView() {
		setSpacing(true);
		
		CodeMirror codeMirror = new CodeMirror("Test Request", CodeStyle.XML);
		codeMirror.setSizeFull();
		addComponent(codeMirror);
		setExpandRatio(codeMirror, 1.0f);
		
		HorizontalLayout bottom = new HorizontalLayout();
		
		channelSelect = new Select("Channel");
		addComponent(channelSelect);
		
		Button sendButton = new Button("Send");
		bottom.addComponent(sendButton);
		
		addComponent(bottom);
	}

	/**
	 * Sets the list of available channels to send test requests to
	 * @param all
	 */
	public void setChannels(Collection<Channel> all) {
		Container container = new BeanItemContainer<Channel>(Channel.class, all);
		channelSelect.setContainerDataSource(container);
	}
}
