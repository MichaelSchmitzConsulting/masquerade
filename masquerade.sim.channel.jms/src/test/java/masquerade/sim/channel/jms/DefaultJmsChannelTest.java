package masquerade.sim.channel.jms;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;
import masquerade.sim.model.Channel;

import org.junit.Test;

public class DefaultJmsChannelTest {
	@Test
	public void testSerialization() {
		Channel source = new DefaultJmsChannel("test");
		Channel target = new DefaultJmsChannel("test");
		assertCanClone(source, target);
	}
}
