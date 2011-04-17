package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.util.ClassUtil;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;

public abstract class CollectionSelectFieldFactory implements FieldFactory {

	private Class<?> type;
	private String defaultWidth;
	private boolean isRequired = true;

	public CollectionSelectFieldFactory(Class<?> type) {
		this(type, null, true);
	}
	
	public CollectionSelectFieldFactory(Class<?> type, String defaultWidth, boolean isRequired) {
	    this.type = type;
	    this.defaultWidth = defaultWidth;
	    this.isRequired = isRequired;
    }

	protected abstract Collection<?> getAll();
	
	@Override
    public Field createField(Object existingValue) {
		Select select = new Select();
		select.setNewItemsAllowed(false);
		if (defaultWidth != null) {
			select.setWidth(defaultWidth);
		}
		select.setCaption(ClassUtil.fromCamelCase(type));
		
		Collection<?> all = getAll();
		fillValues(select, all);
		
		select.select(existingValue);
		select.setRequired(isRequired);
		select.setNullSelectionAllowed(!isRequired);

		return select;
    }

	protected void fillValues(Select select, Collection<?> all) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
        BeanItemContainer<?> container = new BeanItemContainer(type, all);
		select.setContainerDataSource(container);
	}
}
