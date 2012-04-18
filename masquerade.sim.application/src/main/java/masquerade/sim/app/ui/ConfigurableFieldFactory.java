package masquerade.sim.app.ui2;

import java.lang.reflect.Method;

import masquerade.sim.model.Optional;
import masquerade.sim.model.ui.UiConstant;
import masquerade.sim.plugin.FieldFactory;
import masquerade.sim.plugin.PluginRegistry;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;

@Component
@Service(FormFieldFactory.class)
public class ConfigurableFieldFactory extends DefaultFieldFactory {
	private static final long serialVersionUID = 1L;

	private static final String DESCRIPTION = "description";

	@Reference protected PluginRegistry pluginRegistry;
	
	@Override
	public Field createField(Item item, Object propertyId, com.vaadin.ui.Component uiContext) {
		BeanItem<?> beanItem = (BeanItem<?>) item;
		Class<?> itemType = beanItem.getBean().getClass();
		String propertyName = propertyId.toString();
		FieldFactory factory = pluginRegistry.getPropertyEditor(itemType, propertyName);
		if (factory != null) {
			Object bean = beanItem.getBean();
			return factory.createField(bean);
		}
		
		Field field = super.createField(item, propertyId, uiContext);
		field.setWidth(UiConstant.DEFAULT_WIDTH);
		
		if (DESCRIPTION.equals(propertyName) || isOptional(itemType, propertyName)) {
			field.setRequired(false);
		} else {
			field.setRequired(true);
		}
		
		return field;
	}

	private boolean isOptional(Class<?> itemType, String propertyName) {
		// Find getter
		Method getMethod;
		try {
			getMethod = getMethod(itemType, propertyName);
		} catch (NoSuchMethodException e) {
			return false;
		}
		
		// Check for @Optional annotation on getter method
		return getMethod.isAnnotationPresent(Optional.class);
	}

	private static Method getMethod(Class<?> itemType, String propertyName) throws NoSuchMethodException {
		String remainder = propertyName.length() > 1 ? propertyName.substring(1) : "";
		propertyName = propertyName.substring(0, 1).toUpperCase() + remainder;
		
		Method getMethod = null;
		try {
			getMethod = itemType.getMethod("get" + propertyName, new Class[] {});
		} catch (final java.lang.NoSuchMethodException ignored) {
			try {
				getMethod = itemType.getMethod("is" + propertyName, new Class[] {});
			} catch (final java.lang.NoSuchMethodException ignoredAsWell) {
				getMethod = itemType.getMethod("are" + propertyName, new Class[] {});
			}
		}
		return getMethod;
	}
}
