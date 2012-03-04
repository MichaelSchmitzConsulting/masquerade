package masquerade.sim.model.ui;

import java.util.Collection;

import masquerade.sim.model.listener.SaveListener;
import masquerade.sim.util.ContainerUtil;
import masquerade.sim.util.WindowUtil;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CollectionSelectWindow extends Window {

	private final Property property;
	private final TwinColSelect selection;
	private final SaveListener saveListener;

	public CollectionSelectWindow(String caption, Property property, Class<?> containedType, Collection<?> availableSelections, SaveListener saveListener) {
		super(caption);
		
		this.saveListener = saveListener;
		
		setModal(true);
		setWidth("800px");
		
		if (!Collection.class.isAssignableFrom(property.getType())) {
			throw new IllegalArgumentException("Dialog can only handle Collection item types, received " + property.getType().getName());
		}
		
		Collection<?> value = (Collection<?>) property.getValue();
		//BeanItemContainer<?> container = ContainerUtil.collectionContainer(value, containedType);

		// Configure the windws layout; by default a VerticalLayout
		VerticalLayout layout = (VerticalLayout) getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		
		selection = new TwinColSelect();
		BeanItemContainer<?> collectionContainer = ContainerUtil.collectionContainer(availableSelections, containedType);
		selection.setContainerDataSource(collectionContainer);
		selection.setValue(value);
		selection.setSizeFull();
		layout.addComponent(selection);
		layout.setExpandRatio(selection, 1.0f);
		
		// Close button
		Button close = new Button("Save", new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				onSave();
			}
		});

		// The components added to the window are actually added to the window's
		// layout; you can use either. Alignments are set using the layout
		layout.addComponent(close);
		layout.setComponentAlignment(close, Alignment.TOP_RIGHT);
		
		this.property = property;
	}

	protected void onSave() {
		// Close the window by removing it from the parent window
		WindowUtil.getRoot(getWindow()).removeWindow(this);
		Collection<?> itemIds = (Collection<?>) selection.getValue();
		
		@SuppressWarnings("unchecked")
		Collection<Object> value = (Collection<Object>) property.getValue();
		value.clear();
		value.addAll(itemIds);
		
		property.setValue(value);
		
		saveListener.onSave(value);
	}
}
