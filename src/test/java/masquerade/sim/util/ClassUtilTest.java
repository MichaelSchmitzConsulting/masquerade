package masquerade.sim.util;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class ClassUtilTest {

	@Test
	public void testUnqualifiedName() {
		assertEquals("String", ClassUtil.unqualifiedName(""));
	}

	@Test
	public void testFromCamelCase() {
		assertEquals("Concurrent Hash Map", ClassUtil.fromCamelCase(ConcurrentHashMap.class));
		assertEquals("String", ClassUtil.fromCamelCase(String.class));
	}

}
