package masquerade.sim.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.vaadin.codemirror.client.ui.CodeStyle;

import masquerade.sim.db.ModelRepository;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.ResponseSimulation;
import masquerade.sim.model.Script;
import masquerade.sim.model.SimulationStep;
import masquerade.sim.ui.field.CodeMirrorFieldFactory;
import masquerade.sim.ui.field.CollectionSelectWindow;
import masquerade.sim.ui.field.EditWindowField.WindowFactory;
import masquerade.sim.ui.field.EditWindowFieldFactory;
import masquerade.sim.ui.field.FieldFactory;
import masquerade.sim.ui.field.ModelSelectFieldFactory;
import masquerade.sim.ui.field.TextAreaFieldFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Window;

public class ModelFieldFactory extends DefaultFieldFactory {
	private String defaultWidth = "400px";
	
	private Map<String, FieldFactory> factories = new HashMap<String, FieldFactory>();
	
	public ModelFieldFactory(ModelRepository modelRepository) {
		factories.put(
				"responseSimulation",
				new ModelSelectFieldFactory(modelRepository, ResponseSimulation.class, defaultWidth));
		factories.put(
				"requestIdProvider",
				new ModelSelectFieldFactory(modelRepository, RequestIdProvider.class, defaultWidth, false));
		factories.put(
				"script",
				new ModelSelectFieldFactory(modelRepository, Script.class, defaultWidth));
		factories.put(
				"simulationSteps",
				new EditWindowFieldFactory("Steps", this, SimulationStep.class, modelRepository.getModelImplementations(SimulationStep.class)));
		factories.put(
				"javaScript", 
				new CodeMirrorFieldFactory("Script", CodeStyle.JAVASCRIPT));
		factories.put(
				"groovyScript", 
				new TextAreaFieldFactory("Script"));
		factories.put(
				"rubycript", 
				new TextAreaFieldFactory("Script"));
		
		WindowFactory wf = new RequestMappingSelectWindowFactory("Add/Remove Request Mappings", modelRepository);
		factories.put(
			"requestMappings",
			new EditWindowFieldFactory("Request Mappings", this, RequestMapping.class, null, wf));
	}
	
	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		FieldFactory factory = factories.get(propertyId.toString());
		if (factory != null) {
			Object bean = ((BeanItem<?>) item).getBean();
			return factory.createField(bean);
		}
		
		Field field = super.createField(item, propertyId, uiContext);
		if (defaultWidth != null) {
			field.setWidth(defaultWidth);
		}
		
		if ("description".equals(propertyId.toString())) {
			field.setRequired(false);
		} else {
			field.setRequired(true);
		}
		
		return field;
	}

	private static class RequestMappingSelectWindowFactory implements WindowFactory {
		private String caption;
		private ModelRepository repo;
		public RequestMappingSelectWindowFactory(String caption, ModelRepository repo) {
			this.caption = caption;
			this.repo = repo;
		}
		@Override public Window createWindow(Property property, Class<?> containedType, FormFieldFactory fieldFactory) {
			Collection<?> selections = repo.getRequestMappings();
			return new CollectionSelectWindow(caption, property, containedType, selections);
		}
	}
}
