package masquerade.sim.util;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;

public class ContainerUtil {

	public static <T> BeanItemContainer<T> collectionContainer(Collection<T> collection, Class<?> type) {
		@SuppressWarnings("unchecked") // Makes it possible to pass List<X<?>>, X.class into container()
	    Class<T> cast = (Class<T>) type;
		return new BeanItemContainer<T>(cast, collection);
	}

}
