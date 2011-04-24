package masquerade.sim.ui.field;

import java.util.Collection;

import masquerade.sim.db.ModelRepository;
import masquerade.sim.util.ClassUtil;

import org.apache.commons.lang.ClassUtils;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;

public class ModelImplementationSelectFieldFactory implements FieldFactory {

	private Class<?> type;
	private String defaultWidth;
	private ModelRepository modelRepository;

	public ModelImplementationSelectFieldFactory(ModelRepository modelRepository, Class<?> type, String defaultWidth) {
	    this.type = type;
	    this.defaultWidth = defaultWidth;
	    this.modelRepository = modelRepository;
    }

	@Override
	public Field createField(Object existingValue) {
		Select select = new Select();
		select.setNewItemsAllowed(false);
		if (defaultWidth != null) {
			select.setWidth(defaultWidth);
		}
		select.setCaption(ClassUtil.fromCamelCase(type));
		
		Collection<Class<?>> all = modelRepository.getModelImplementations(type);
		fillValues(select, all);
		
		select.select(existingValue);
		select.setNullSelectionAllowed(false);
		select.setNewItemsAllowed(false);
		select.setRequired(true);
		
		return select;
	}

	protected void fillValues(Select select, Collection<Class<?>> all) {
        IndexedContainer container = new IndexedContainer(all);
		select.setContainerDataSource(container);
		for (Class<?> type : all) {
			select.setItemCaption(type, ClassUtils.getShortClassName(type));
		}
	}
}
