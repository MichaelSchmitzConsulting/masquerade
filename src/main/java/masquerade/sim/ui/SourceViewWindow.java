package masquerade.sim.ui;

import masquerade.sim.util.WindowUtil;

import org.vaadin.codemirror.CodeMirror;
import org.vaadin.codemirror.client.ui.CodeStyle;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SourceViewWindow extends Window {
	private TextField view;
	
	public static void showModal(Window parent, String caption, String content, CodeStyle style) {
		SourceViewWindow window = new SourceViewWindow(caption, style);
		window.setContent(content);
		WindowUtil.getRoot(parent).addWindow(window);
	}
	
	private SourceViewWindow(String caption, CodeStyle style) {
		super(caption);
		
		setModal(true);
		setWidth("600px");
		setHeight("400px");
		
		VerticalLayout layout = (VerticalLayout) getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		if (style == null) {
			view = new TextField();
		} else {
			view = new CodeMirror(caption, style);
		}
		view.setSizeFull();
		layout.addComponent(view);
		layout.setExpandRatio(view, 1.0f);
		
		// Close button
		Button close = new Button("OK", new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				close();
			}
		});
		
		layout.addComponent(close);
		layout.setComponentAlignment(close, Alignment.TOP_RIGHT);
	}
	
	public void setContent(String content) {
		view.setValue(content);
	}
}
