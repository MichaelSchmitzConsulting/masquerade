package masquerade.sim.model.impl;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;

import org.junit.Test;

public class XPathRequestIdProviderTest {
    @Test
    public void testClone() {
        assertCanClone(new XPathRequestIdProvider());
    }
}