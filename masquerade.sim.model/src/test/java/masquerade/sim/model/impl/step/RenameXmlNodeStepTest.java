package masquerade.sim.model.impl.step;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;

import org.junit.Test;

public class RenameXmlNodeStepTest {
    @Test
    public void testClone() {
        assertCanClone(new RenameXmlNodeStep());
    }
}