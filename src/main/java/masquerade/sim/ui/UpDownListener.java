package masquerade.sim.ui;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * A {@link ClickListener} for up/down buttons. Provides the ability
 * to move items in an {@link Indexed} container up and down.
 */
public class UpDownListener implements ClickListener {
	private AbstractSelect viewer;
	private final boolean isDown;

	public UpDownListener(AbstractSelect viewer, boolean isDown) {
		this.viewer = viewer;
		this.isDown = isDown;
	}

	@Override public void buttonClick(ClickEvent event) {
		Object selectionId = viewer.getValue();
		Container obj = (BeanItemContainer<?>) viewer.getContainerDataSource();
		if (obj instanceof Indexed) {
			Indexed container = (Indexed) obj;
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
}