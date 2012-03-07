package masquerade.sim.app.ui2.view.impl;

import java.util.List;

import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.ui.MasterDetailView;
import masquerade.sim.model.ui.MasterDetailView.AddListener;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.VerticalLayout;

/**
 * Master/detail view showing simulations
 */
@SuppressWarnings("serial")
public class SimulationViewImpl extends VerticalLayout implements SimulationView {

	private final MasterDetailView masterDetailView;
	private SimulationViewCallback callback;

	public SimulationViewImpl() {
		setCaption("Simulations");
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		masterDetailView = new MasterDetailView();
		addComponent(masterDetailView);
		setExpandRatio(masterDetailView, 1.0f);
		
		masterDetailView.addAddListener(new AddListener() {
			@Override public void onAdd() {
				callback.onAdd();
			}
		});
		masterDetailView.addDeleteListener(new DeleteListener() {
			@Override public void notifyDelete(Object obj) {
				String simulationId = (String) masterDetailView.getSelection();
				callback.onRemove(simulationId);
			}
		});
	}
	
	@Override
	public void setSimulationList(List<String> simulations) {
		Container container = new IndexedContainer(simulations);
		masterDetailView.setDataSource(container);
	}
	
	@Override
	public void setCurrentSimulation(Simulation simulation) {
		// TODO Auto-generated method stub
	}

	public void bind(SimulationViewCallback callback) {
		this.callback = callback;
	}
}
