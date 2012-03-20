package masquerade.sim.model.impl;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;

import org.junit.Test;

public class SequenceScriptTest {
    @Test
    public void testClone() {
        assertCanClone(new SequenceScript());
    }
}