package masquerade.sim.plugin;

import com.vaadin.ui.Field;

public interface FieldFactory {
	Field createField(Object existingValue);
}
