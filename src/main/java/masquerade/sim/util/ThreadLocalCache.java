package masquerade.sim.util;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A cache keeping values per thread as long as the thread lives.
 * Keeps weak refs to the thread and a strong ref to the value.
 * 
 * @param <T> Cached value type
 */
public class ThreadLocalCache<T> {
	private Map<Thread, T> map = Collections.synchronizedMap( 
		new WeakHashMap<Thread, T>());
	
	public void put(T value) {
		map.put(Thread.currentThread(), value);
	}
	
	public T get() {
		return map.get(Thread.currentThread());
	}
	
	public void clear() {
		map.clear();
	}
}
