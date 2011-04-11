package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.util.ClassUtil;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;

public abstract class CollectionSelectFieldFactory implements FieldFactory {

	private Class<?> type;
	private String defaultWidth;

	public CollectionSelectFieldFactory(Class<?> type) {
		this(type, null);
	}
	
	public CollectionSelectFieldFactory(Class<?> type, String defaultWidth) {
	    this.type = type;
	    this.defaultWidth = defaultWidth;
    }

	protected abstract Collection<?> getAll();
	
	@Override
    public final Field createField(Object existingValue) {
		Select select = new Select();
		select.setNewItemsAllowed(false);
		if (defaultWidth != null) {
			select.setWidth(defaultWidth);
		}
		select.setCaption(ClassUtil.fromCamelCase(type));
		
		Collection<?> simulations = getAll();
		@SuppressWarnings({ "unchecked", "rawtypes" })
        BeanItemContainer<?> container = new BeanItemContainer(type, simulations);
		select.setContainerDataSource(container);
		
		select.select(existingValue);
		select.setRequired(true);
		
		return select;
    }
}
