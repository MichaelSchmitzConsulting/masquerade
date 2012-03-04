package masquerade.sim.model.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * A {@link ClickListener} for up/down buttons. Provides the ability
 * to move items in an {@link Indexed} container up and down. Set
 * the select UI element provided to this listener to immediate
 * to get button enablement to work correctly.
 */
@SuppressWarnings("serial")
public class UpDownListener implements ClickListener, ValueChangeListener {
	private AbstractSelect viewer;
	private Button up;
	private Button down;

	public static UpDownListener install(AbstractSelect viewer, Button up, Button down) {
		UpDownListener listener = new UpDownListener(viewer, up, down);
		up.addListener((ClickListener) listener);
		down.addListener((ClickListener) listener);
		viewer.addListener(listener);
		return listener;
	}

	/**
	 * Creates a new up/down listener
	 * @param viewer
	 * @param up
	 * @param down
	 */
	private UpDownListener(AbstractSelect viewer, Button up, Button down) {
		this.viewer = viewer;
		this.up = up;
		this.down = down;
	}

	/**
	 * Handles clicks on up/down buttons, and moves the currently selected
	 * item in the container accordingly.
	 * @see ClickListener
	 */
	@Override 
	public void buttonClick(ClickEvent event) {
		Object selectionId = viewer.getValue();
		if (selectionId == null) {
			return;
		}
		
		Indexed container = getContainer();
		if (container != null) {
			doMove(selectionId, container, event.getButton() == down);
		}
		
		updateButtonState(selectionId);
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		Object value = event.getProperty().getValue();
		updateButtonState(value);
	}

	private void updateButtonState(Object selectionId) {
		Indexed container;
		if (selectionId != null && (container = getContainer()) != null) {
			// Selection, enable buttons depending on index
			int index = getIndex(container, selectionId);
			up.setEnabled(index > 0);
			down.setEnabled(index < container.size() - 1);
		} else {
			// No selection - disable up/down buttons
			up.setEnabled(false);
			down.setEnabled(false);
		}
	}

	private int getIndex(Indexed container, Object selectionId) {
		return container.indexOfId(selectionId);
	}

	private Indexed getContainer() {
		Container obj = (BeanItemContainer<?>) viewer.getContainerDataSource();
		if (obj instanceof Indexed) {
			return (Indexed) obj;
		} else {
			return null;
		}
	}

	/**
	 * Moves an item in the given indexed container up or down,
	 * depending on {@link #isDown}.
	 * @param selectionId
	 * @param container
	 */
	private static void doMove(Object selectionId, Indexed container, boolean isDown) {
		int index = container.indexOfId(selectionId);
		if (isDown && index < container.size() - 1) {
			container.removeItem(selectionId);
			container.addItemAt(index + 1, selectionId);
		} else if (!isDown && index > 0) {
			container.removeItem(selectionId);
			container.addItemAt(index - 1, selectionId);
		}
	}
}