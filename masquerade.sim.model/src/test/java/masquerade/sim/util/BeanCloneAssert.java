package masquerade.sim.util;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;

/**
 * Utility to test wheter model beans can be cloned using {@link BeanCopier}.
 * Cloning is considered succesful if no exceptions occur when invoking
 * {@link BeanCopier#copyBean(Object)}, and if the resulting bean
 * is not <code>null</code>. Cloned beans are thus not required to implement
 * equals, and if bean cloning as such works is tests in {@link BeanCopier}
 * tests.
 */
public class BeanCloneAssert {
	public static void assertCanClone(Object source, Object target) {
		new DefaultBeanCopier().copyBean(source, target);
	}

	public static void assertCanClone(Object object) {
		Object result = new DefaultBeanCopier().copyBean(object);
		assertNotNull(result);
		assertSame(object.getClass(), result.getClass());
	}
}
