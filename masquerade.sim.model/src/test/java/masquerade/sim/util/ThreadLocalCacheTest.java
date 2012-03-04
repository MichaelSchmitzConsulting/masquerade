package masquerade.sim.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThreadLocalCacheTest {

	@Test
	public void testPut() {
		ThreadLocalCache<String> cache = new ThreadLocalCache<String>();
		
		assertNull(cache.get());
		
		cache.put("a");
		assertEquals("a", cache.get());
		assertEquals("a", cache.get());

		cache.put("b");
		assertEquals("b", cache.get());
		assertEquals("b", cache.get());
		
		cache.clear();
		assertNull(cache.get());
	}

}
