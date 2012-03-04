package masquerade.sim.app.binding;

import java.util.List;

import masquerade.sim.model.history.HistoryEntry;
import masquerade.sim.model.history.RequestHistory;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;

/**
 * A {@link ContainerFactory} creating a Vaadin data {@link Container}
 * containing the latest 10000 request history entries.
 */
public class RequestHistoryContainerFactory implements ContainerFactory {

	private static final int MAX_AMOUNT = 10000;
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
		List<HistoryEntry> initialContent = requestHistory.getLatestRequests(MAX_AMOUNT);
		BeanItemContainer<?> container = new BeanItemContainer<HistoryEntry>(HistoryEntry.class, initialContent);
		return container;
	}
}
