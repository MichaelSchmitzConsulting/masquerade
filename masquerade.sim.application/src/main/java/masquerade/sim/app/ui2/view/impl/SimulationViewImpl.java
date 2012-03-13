package masquerade.sim.app.ui2.view.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.app.util.BeanUiUtils;
import masquerade.sim.model.RequestIdProvider;
import masquerade.sim.model.RequestMapping;
import masquerade.sim.model.Script;
import masquerade.sim.model.Simulation;

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Master/detail view showing simulations
 */
@SuppressWarnings("serial")
public class SimulationViewImpl extends VerticalLayout implements SimulationView {

	private static final String LIST_WIDTH = "200px";

	private final FormFieldFactory fieldFactory;
	private SimulationViewCallback callback;
	private Table simulationList;
	private VerticalLayout channelTab;
	private VerticalLayout scriptTab;
	private VerticalLayout idProviderTab;
	private VerticalLayout selectorTab;
	private VerticalLayout namespacesTab;

	private VerticalLayout detailLayout;

	private TwinColSelect channelSelect;

	
	public SimulationViewImpl(FormFieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
		
		setCaption("Simulations");
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		// Create layout
	    HorizontalLayout mainLayout = new HorizontalLayout();
	    
	    // Top-level component properties
	    setSizeFull();
	    
	    VerticalLayout leftLayout = new VerticalLayout();
	    leftLayout.setHeight("100%");
		
        simulationList = new Table(null, null);
        simulationList.setSelectable(true);
        simulationList.setMultiSelect(false);
        simulationList.setImmediate(true);
        simulationList.setNullSelectionAllowed(true);
        simulationList.setWidth(LIST_WIDTH);
        simulationList.setHeight("100%");
        simulationList.addListener(new ValueChangeListener() {
			@Override public void valueChange(ValueChangeEvent event) {
				SimulationInfo simInfo = (SimulationInfo) event.getProperty().getValue();
				if (simInfo == null) {
					callback.onSimulationSelected(null);
				} else {
					callback.onSimulationSelected(simInfo.getSimulation());					
				}
			}
		});
        leftLayout.addComponent(simulationList);
        leftLayout.setExpandRatio(simulationList, 1.0f);
		
        // Add button
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);
        Button addButton = new Button("Add");
        addButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				callback.onAdd();
			}
		});
		buttonLayout.addComponent(addButton);

        // Remove button
        final Button removeButton = new Button("Remove");
        removeButton.setEnabled(false);
        removeButton.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				SimulationInfo simInfo = (SimulationInfo) simulationList.getValue();
				callback.onRemove(simInfo.getSimulation());
			}
		});
        simulationList.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				removeButton.setEnabled(event.getProperty().getValue() != null);
			}
		});
		buttonLayout.addComponent(removeButton);
		
		// Spacer on the right of buttons
		Component spacer = new Label();
		buttonLayout.addComponent(spacer);
		buttonLayout.setExpandRatio(spacer, 1.0f);

		leftLayout.addComponent(buttonLayout);
        leftLayout.setWidth(LIST_WIDTH);
	    mainLayout.addComponent(leftLayout);

	    detailLayout = new VerticalLayout();
	    detailLayout.setSpacing(true);
	    
	    TabSheet tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);

        channelTab = new VerticalLayout();
		tabSheet.addTab(channelTab, "Channel");
        scriptTab = new VerticalLayout();
		tabSheet.addTab(scriptTab, "Script");
        selectorTab = new VerticalLayout();
        tabSheet.addTab(selectorTab, "Selector");
        idProviderTab = new VerticalLayout();
        tabSheet.addTab(idProviderTab, "Request ID Provider");
        namespacesTab = new VerticalLayout();
        tabSheet.addTab(namespacesTab, "Namespaces");
        detailLayout.addComponent(tabSheet);
        
        // Save button
        Button saveButton = new Button("Save", new ClickListener() {
			@Override public void buttonClick(ClickEvent event) {
				callback.onSave();
				getWindow().showNotification("Saved", "Simulation updated");
			}
		});
        detailLayout.addComponent(saveButton);
        detailLayout.setVisible(false); // Initially invisible, until a simulation is selected
        
        mainLayout.addComponent(detailLayout);
	    mainLayout.setExpandRatio(detailLayout, 1.0f);
	    
	    mainLayout.setSizeFull();
	    mainLayout.setSpacing(true);
	    
	    addComponent(mainLayout);
	}
	
	public void bind(SimulationViewCallback callback) {
		this.callback = callback;
		callback.onRefresh();
	}

	@Override
	public void setSimulationList(List<SimulationInfo> simulations) {
		Container container = new BeanItemContainer<SimulationInfo>(SimulationInfo.class, simulations);
		simulationList.setContainerDataSource(container);
		simulationList.setVisibleColumns(new Object[] { "simulation" });
	}
	
	@Override
	public void deselectSimulation() {
		setCurrentSimulation(null, null, null);
	}

	@Override
	public void setCurrentSimulation(Simulation simulation, Collection<String> allChannels, Collection<String> assignedToChannels) {
		if (simulation == null) {
			detailLayout.setVisible(false);
		} else {
			detailLayout.setVisible(true);
			setFormToTab(createChannelAssignmentView(allChannels, assignedToChannels), channelTab);
			
			Script script = simulation.getScript();
			setFormToTab(BeanUiUtils.createForm(script, fieldFactory), scriptTab);
			
			RequestMapping<?> requestMapping = simulation.getSelector();
			setFormToTab(BeanUiUtils.createForm(requestMapping, fieldFactory), selectorTab);
			
			RequestIdProvider<?> idProvider = simulation.getRequestIdProvider();
			setFormToTab(BeanUiUtils.createForm(idProvider, fieldFactory), idProviderTab);
			
			Label namespaces = new Label("TODO: Namespaces");
			setFormToTab(namespaces, namespacesTab);
		}
	}

	@Override
	public Collection<String> getChannelAssignments() {
		@SuppressWarnings("unchecked")
		Collection<String> channelIds = (Collection<String>) channelSelect.getValue();
		return channelIds;
	}

	private Component createChannelAssignmentView(Collection<String> allChannels, Collection<String> assignedToChannels) {
		channelSelect = new TwinColSelect();
        for (String channel : sort(allChannels)) {
            channelSelect.addItem(channel);
        }
        channelSelect.setValue(sort(assignedToChannels));
        
        channelSelect.setRows(10);
        channelSelect.setNullSelectionAllowed(true);
        channelSelect.setMultiSelect(true);
        channelSelect.setImmediate(true);
        channelSelect.setLeftColumnCaption("Available channels");
        channelSelect.setRightColumnCaption("Active on channels");
        channelSelect.setWidth("400px");
        
        return channelSelect;
	}

	private List<String> sort(Collection<String> allChannels) {
		List<String> sortedChannels = new ArrayList<String>(allChannels);
		Collections.sort(sortedChannels);
		return sortedChannels;
	}

	private static void setFormToTab(Component component, VerticalLayout tabLayout) {
		// Wrap form into a panel to get scroll bars if it cannot be displayed fully
	    Panel panel = new Panel();
		panel.setSizeFull();
		panel.addStyleName(Reindeer.PANEL_LIGHT);
		tabLayout.removeAllComponents();
		tabLayout.addComponent(panel);
		
		Layout panelLayout = new VerticalLayout();
		panelLayout.setMargin(true);
		panelLayout.addComponent(component);
		panelLayout.setSizeFull();
		panel.setContent(panelLayout);
	}

	@Override
	public void setSelection(String id) {
		if (id == null) {
			simulationList.setValue(null);
		} else {
			simulationList.setValue(new SimulationInfo(id));			
		}
	}

	/** Bean to show in Master/Detail view's master table */
	public static final class SimulationInfo {
		private final String id;
		private Boolean isPersistent;
		public SimulationInfo(String id, boolean isPersistent) {
			this.id = id;
			this.isPersistent = isPersistent;
		}
		/** Constructor for settings selection, persistent flag is not taken into account in equals */
		private SimulationInfo(String id) {
			this.id = id;
		}
		public String getSimulation() {
			return id;
		}
		public boolean isPersistent() {
			return isPersistent;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimulationInfo other = (SimulationInfo) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
}
