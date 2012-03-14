package masquerade.sim.channel.jms;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;
import masquerade.sim.model.Channel;

import org.junit.Test;

public class WebSphereMqJmsChannelTest {
	@Test
	public void testSerialization() {
		Channel source = new WebSphereMqJmsChannel("test");
		Channel target = new WebSphereMqJmsChannel("test");
		assertCanClone(source, target);
	}
}
