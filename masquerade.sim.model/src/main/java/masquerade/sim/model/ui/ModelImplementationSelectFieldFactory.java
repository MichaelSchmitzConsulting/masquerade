package masquerade.sim.model.ui;

import java.util.Collection;

import masquerade.sim.plugin.FieldFactory;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.util.ClassUtil;

import org.apache.commons.lang.ClassUtils;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;

/** 
 * A {@link FieldFactory} creating a {@link Select} dropdown
 * allowing the user to select from a list of classes that
 * implement a certain interface (e.g. implementations
 * of the {@link masquerade.sim.model.Channel} simulation
 * model interface.
 */
public class ModelImplementationSelectFieldFactory implements FieldFactory {

	private final Class<?> type;
	private final String defaultWidth;
	private final PluginRegistry pluginRegistry;

	public ModelImplementationSelectFieldFactory(Class<?> type, String defaultWidth, PluginRegistry pluginRegistry) {
	    this.type = type;
	    this.defaultWidth = defaultWidth;
	    this.pluginRegistry = pluginRegistry;
    }

	@Override
	public Field createField(Object existingValue) {
		Select select = new Select();
		select.setNewItemsAllowed(false);
		if (defaultWidth != null) {
			select.setWidth(defaultWidth);
		}
		select.setCaption(ClassUtil.fromCamelCase(type));
		
		Collection<Class<?>> all = pluginRegistry.getExtensions(type);
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
