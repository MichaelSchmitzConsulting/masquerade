package masquerade.sim.model.impl.step;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;

import org.junit.Test;

public class JavaScriptStepTest {
    @Test
    public void testClone() {
        assertCanClone(new JavaScriptStep());
    }
}