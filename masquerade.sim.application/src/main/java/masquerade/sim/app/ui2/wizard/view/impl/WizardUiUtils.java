package masquerade.sim.app.ui2.wizard.view.impl;

import java.util.Collection;

import masquerade.sim.util.ClassUtil;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Select;

public class WizardUiUtils {
	public static Select createTypeSelectDropdown(Collection<Class<?>> instanceTypes) {
		Select selector = new Select("Type");
		selector.setNewItemsAllowed(false);
		selector.setRequired(true);
        
		@SuppressWarnings({ "unchecked", "rawtypes" })
		BeanItemContainer<?> container = new BeanItemContainer(Class.class, instanceTypes);
		selector.setContainerDataSource(container);
		for (Class<?> item : instanceTypes) {
			selector.setItemCaption(item, ClassUtil.fromCamelCase(item));
		}
		selector.select(instanceTypes.iterator().next());
		selector.setWidth("250px");
		return selector;
	}
}
