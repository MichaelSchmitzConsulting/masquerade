package masquerade.sim.app.ui2.view.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import masquerade.sim.app.ui2.view.SimulationView;
import masquerade.sim.model.Simulation;
import masquerade.sim.model.listener.DeleteListener;
import masquerade.sim.model.ui.MasterDetailView;
import masquerade.sim.model.ui.MasterDetailView.AddListener;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.VerticalLayout;

/**
 * Master/detail view showing simulations
 */
@SuppressWarnings("serial")
public class SimulationViewImpl extends VerticalLayout implements SimulationView {

	private final MasterDetailView masterDetailView;
	private SimulationViewCallback callback;

	public SimulationViewImpl(FormFieldFactory fieldFactory) {
		setCaption("Simulations");
		setMargin(true);
		setSpacing(true);
		setSizeFull();
		
		masterDetailView = new MasterDetailView(fieldFactory);
		addComponent(masterDetailView);
		setExpandRatio(masterDetailView, 1.0f);
		
		masterDetailView.addAddListener(new AddListener() {
			@Override public void onAdd() {
				callback.onAdd();
			}
		});
		masterDetailView.addDeleteListener(new DeleteListener() {
			@Override public void notifyDelete(Object obj) {
				SimulationInfo simulation = (SimulationInfo) masterDetailView.getSelection();
				callback.onRemove(simulation.getId());
			}
		});
	}
	
	@Override
	public void setSimulationList(List<String> simulationIds) {
		Collection<SimulationInfo> simulations = new ArrayList<SimulationInfo>(simulationIds.size());
		for (String id : simulationIds) {
			simulations.add(new SimulationInfo(id));
		}
		Container container = new BeanItemContainer<SimulationInfo>(SimulationInfo.class, simulations);
		masterDetailView.setDataSource(container);
	}
	
	@Override
	public void setCurrentSimulation(Simulation simulation) {
		// TODO: Show edit part, remove Master/Detail view
		masterDetailView.setSelection(new SimulationInfo(simulation.getId()));
	}

	public void bind(SimulationViewCallback callback) {
		this.callback = callback;
	}
	
	/** Bean to show in Master/Detail view's master table */
	public static final class SimulationInfo {
		private final String id;
		public SimulationInfo(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
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
