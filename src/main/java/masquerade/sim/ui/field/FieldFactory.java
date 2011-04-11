package masquerade.sim.ui.field;

import com.vaadin.ui.Field;

public interface FieldFactory {
	Field createField(Object existingValue);
}
