package masquerade.sim.util;

/**
 * A cache keeping values per thread as long as the thread lives.
 * Keeps weak refs to the thread and a strong ref to the value.
 * 
 * @param <T> Cached value type
 */
public class ThreadLocalCache<T> {
	private volatile ThreadLocal<T> cache = new ThreadLocal<T>();
	
	public void put(T value) {
		cache.set(value);
	}
	
	public T get() {
		return cache.get();
	}

	public void clear() {
		cache = new ThreadLocal<T>();
	}
}
