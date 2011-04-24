package masquerade.sim.ui;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * A view displaying status messages from the simulation engine:
 * Informations, warnings, errors.
 */
public class StatusView extends VerticalLayout {
	public StatusView() {
		buildLayout();
	}

	private void buildLayout() {
		Table table = new Table();
		addComponent(table);
		setExpandRatio(table, 1.0f);
	}
}
