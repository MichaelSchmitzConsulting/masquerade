package masquerade.sim.ui;

import java.util.List;

import masquerade.sim.history.HistoryEntry;
import masquerade.sim.history.RequestHistory;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;

public class RequestHistoryContainerFactory implements ContainerFactory {

	private RequestHistory requestHistory;

	public RequestHistoryContainerFactory(RequestHistory requestHistory) {
		this.requestHistory = requestHistory;
	}

	@Override
	public Class<?> getType() {
		return HistoryEntry.class;
	}

	@Override
	public Container createContainer() {
		List<HistoryEntry> initialContent = requestHistory.getLatestRequests(100);
		BeanItemContainer<?> container = new BeanItemContainer<HistoryEntry>(HistoryEntry.class, initialContent);
		return container;
	}

}
