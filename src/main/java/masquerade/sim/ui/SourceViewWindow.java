package masquerade.sim.ui;

import masquerade.sim.util.WindowUtil;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class SourceViewWindow extends Window {
	private TextField view;
	
	public static void showModal(Window parent, String caption, String content) {
		SourceViewWindow window = new SourceViewWindow(caption);
		window.setContent(content);
		WindowUtil.getRoot(parent).addWindow(window);
	}
	
	private SourceViewWindow(String caption) {
		super(caption);
		
		setModal(true);
		setWidth("800px");
		setHeight("600px");
		
		VerticalLayout layout = (VerticalLayout) getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		view = new TextField();
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
