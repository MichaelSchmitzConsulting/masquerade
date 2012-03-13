package masquerade.sim.model;

import masquerade.sim.model.impl.DefaultSimulation;
import masquerade.sim.model.impl.DynamicScript;
import masquerade.sim.model.impl.ProvidedResponse;
import masquerade.sim.model.impl.SequenceScript;
import masquerade.sim.model.impl.SoapFaultStep;
import masquerade.sim.model.impl.XPathRequestIdProvider;
import masquerade.sim.model.impl.XPathRequestMapping;
import masquerade.sim.model.impl.XpathAlternativesRequestIdProvider;
import masquerade.sim.model.impl.step.AbstractScriptedStep;
import masquerade.sim.model.impl.step.AddChildElementsStep;
import masquerade.sim.model.impl.step.AddResponseElementStep;
import masquerade.sim.model.impl.step.ExtractXpathToVariableStep;
import masquerade.sim.model.impl.step.GroovyScriptStep;
import masquerade.sim.model.impl.step.JavaScriptStep;
import masquerade.sim.model.impl.step.LoadTemplateStep;
import masquerade.sim.model.impl.step.LoadTemplateToVariableStep;
import masquerade.sim.model.impl.step.RemoveXmlNodeStep;
import masquerade.sim.model.impl.step.RenameRootElementStep;
import masquerade.sim.model.impl.step.RenameXmlNodeStep;
import masquerade.sim.model.impl.step.ReplaceElementStep;
import masquerade.sim.model.impl.step.RubyScriptStep;
import masquerade.sim.model.impl.step.ScriptFileStep;
import masquerade.sim.model.impl.step.SendIntermediateResponseStep;
import masquerade.sim.model.impl.step.SetResponseContentStep;
import masquerade.sim.model.impl.step.WaitStep;
import masquerade.sim.model.ui.CodeMirrorFieldFactory;
import masquerade.sim.model.ui.EditWindowFieldFactory;
import masquerade.sim.model.ui.FileSelectFieldFactory;
import masquerade.sim.model.ui.HtmlViewFieldFactory;
import masquerade.sim.model.ui.InstanceTypeProvider;
import masquerade.sim.model.ui.TextAreaFieldFactory;
import masquerade.sim.model.ui.UiConstant;
import masquerade.sim.plugin.PluginRegistry;
import masquerade.sim.status.StatusLog;
import masquerade.sim.status.StatusLogger;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import org.vaadin.codemirror2.client.ui.CodeMode;

import com.vaadin.ui.FormFieldFactory;

/**
 * Registers the default Masquerade model extensions
 */
@Component
public class ModelInitializer {

	private final static StatusLog log = StatusLogger.get(ModelInitializer.class);
	
	@Reference protected PluginRegistry pluginRegistry;
	@Reference protected FileLoader fileLoader;
	@Reference protected FormFieldFactory fieldFactory;
	
	@Activate
	protected void activate(ComponentContext componentContext) {
		registerExtensions();
		registerFieldFactories();
		
		log.info("Model bundle initialized");
	}

	private void registerExtensions() {
		// Simulation
		pluginRegistry.registerExtension(Simulation.class, DefaultSimulation.class);
		
		// Scripts
		pluginRegistry.registerExtension(Script.class, SequenceScript.class);
		pluginRegistry.registerExtension(Script.class, ProvidedResponse.class);
		pluginRegistry.registerExtension(Script.class, DynamicScript.class);
		
		// Request ID Providers
		pluginRegistry.registerExtension(RequestIdProvider.class, XPathRequestIdProvider.class);
		pluginRegistry.registerExtension(RequestIdProvider.class, XpathAlternativesRequestIdProvider.class);
		
		// Request Mapping
		pluginRegistry.registerExtension(RequestMapping.class, XPathRequestMapping.class);
		
		// Simulation Steps
		pluginRegistry.registerExtension(SimulationStep.class, AddChildElementsStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, AddResponseElementStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, CopyRequestToResponseStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, ExtractXpathToVariableStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, GroovyScriptStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, JavaScriptStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, LoadTemplateStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, LoadTemplateToVariableStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, RenameXmlNodeStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, ReplaceElementStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, RemoveXmlNodeStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, RenameRootElementStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, RubyScriptStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, ScriptFileStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, SendIntermediateResponseStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, SetResponseContentStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, SoapFaultStep.class);
		pluginRegistry.registerExtension(SimulationStep.class, WaitStep.class);
		
		// Namespace prefix
		pluginRegistry.registerExtension(NamespacePrefix.class, NamespacePrefix.class);
	}

	private void registerFieldFactories() {
		InstanceTypeProvider instanceTypeProvider = new ModelInstanceTypeProvider(SimulationStep.class, pluginRegistry);
		EditWindowFieldFactory stepEditorFactory = new EditWindowFieldFactory("Steps", fieldFactory, SimulationStep.class, instanceTypeProvider);
		pluginRegistry.registerPropertyEditor(
				SequenceScript.class,
				"simulationSteps",
				stepEditorFactory);
		
		pluginRegistry.registerPropertyEditor(
				JavaScriptStep.class,
				"javaScript", 
				new CodeMirrorFieldFactory("Script", CodeMode.JAVASCRIPT));
		pluginRegistry.registerPropertyEditor(
				GroovyScriptStep.class,
				"groovyScript", 
				new CodeMirrorFieldFactory("Script", CodeMode.JAVA));
		pluginRegistry.registerPropertyEditor(
				RubyScriptStep.class,
				"rubyScript", 
				new CodeMirrorFieldFactory("Script", CodeMode.JAVA));
		pluginRegistry.registerPropertyEditor(
				LoadTemplateStep.class,
				"templateName",
				new FileSelectFieldFactory("Template", fileLoader, FileType.TEMPLATE, UiConstant.DEFAULT_WIDTH));
		pluginRegistry.registerPropertyEditor(
				XpathAlternativesRequestIdProvider.class,
				"xpaths",
				new TextAreaFieldFactory("XPaths (per line)"));
		
		pluginRegistry.registerPropertyEditor(
				AbstractScriptedStep.class,
				"documentation",
				new HtmlViewFieldFactory("Documentation"));	
	}
}
