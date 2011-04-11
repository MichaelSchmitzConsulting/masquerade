package masquerade.sim.ui.field;

import java.util.Collection;

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

public class CollectionSelectWindow extends Window {

	private Property property;
	private TwinColSelect selection;

	public CollectionSelectWindow(String caption, Property property, Class<?> containedType, Collection<?> availableSelections) {
		super(caption);
		
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
		Button close = new Button("OK", new Button.ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				onClose();
			}
		});

		// The components added to the window are actually added to the window's
		// layout; you can use either. Alignments are set using the layout
		layout.addComponent(close);
		layout.setComponentAlignment(close, Alignment.TOP_RIGHT);
		
		this.property = property;
	}

	protected void onClose() {
		// close the window by removing it from the parent window
		WindowUtil.getRoot(getWindow()).removeWindow(this);

//		BeanItemContainer<?> dataSource = (BeanItemContainer<?>) masterDetailView.getDataSource();
		
		// In a BeanItemContainer, beans == ids, thus getItemIds() returns all beans in the container 
//		Collection<?> itemIds = dataSource.getItemIds();
		Collection<?> itemIds = (Collection<?>) selection.getValue();
		
		property.setValue(itemIds);
	}
}
