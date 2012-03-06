package masquerade.sim.app.ui2.view.impl;

import java.util.List;

import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.model.ui.MasterDetailView;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.VerticalLayout;

/**
 * Master/detail view showing simulations
 */
@SuppressWarnings("serial")
public class SimulationViewImpl extends VerticalLayout implements SimulationView {

	private final MasterDetailView masterDetailView;

	public SimulationViewImpl() {
		setCaption("Simulations");
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		masterDetailView = new MasterDetailView();
		addComponent(masterDetailView);
		setExpandRatio(masterDetailView, 1.0f);
	}
	
	@Override
	public void setSimulationList(List<String> simulations) {
		Container container = new IndexedContainer(simulations);
		masterDetailView.setDataSource(container);
	}

}
