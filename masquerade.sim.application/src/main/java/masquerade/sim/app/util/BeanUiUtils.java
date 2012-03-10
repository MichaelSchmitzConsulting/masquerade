package masquerade.sim.app.util;

import java.util.Collection;

import masquerade.sim.util.ClassUtil;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Select;

public class BeanUiUtils {
	public static Form createForm(Object bean, FormFieldFactory fieldFactory) {
		Form form = new Form();
		form.setSizeFull();
		String shortTypeName = bean.getClass().getSimpleName();
		form.setCaption(shortTypeName);
		form.setWriteThrough(true);
		form.setInvalidCommitted(false);
		form.setFormFieldFactory(fieldFactory);

		BeanItem<?> item = new BeanItem<Object>(bean);
		form.setItemDataSource(item);

		form.getLayout().setSizeFull();

		return form;
	}

	public static Select createTypeSelectDropdown(Collection<Class<?>> instanceTypes) {
		Select selector = new Select();
		selector.setNewItemsAllowed(false);
		selector.setRequired(true);
		selector.setImmediate(true);
		selector.setNullSelectionAllowed(false);
		selector.setRequired(false);
        
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
