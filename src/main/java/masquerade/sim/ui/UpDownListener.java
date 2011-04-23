package masquerade.sim.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * A {@link ClickListener} for up/down buttons. Provides the ability
 * to move items in an {@link Indexed} container up and down. Set
 * the select UI element provided to this listener to immediate
 * to get button enablement to work correctly.
 */
public class UpDownListener implements ClickListener {
	private AbstractSelect viewer;
	private final boolean isDown;

	public UpDownListener(AbstractSelect viewer, boolean isDown) {
		this.viewer = viewer;
		this.isDown = isDown;
	}

	/**
	 * Handles clicks on up/down buttons, and moves the currently selected
	 * item in the container accordingly.
	 */
	@Override 
	public void buttonClick(ClickEvent event) {
		Object selectionId = viewer.getValue();
		if (selectionId == null) {
			return;
		}
		
		Container obj = (BeanItemContainer<?>) viewer.getContainerDataSource();
		if (obj instanceof Indexed) {
			Indexed container = (Indexed) obj;
			doMove(selectionId, container);
		}
	}

	/**
	 * Moves an item in the given indexed container up or down,
	 * depending on {@link #isDown}.
	 * @param selectionId
	 * @param container
	 */
	private void doMove(Object selectionId, Indexed container) {
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