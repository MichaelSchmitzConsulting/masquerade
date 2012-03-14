package masquerade.sim.channel.http;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;
import masquerade.sim.model.Channel;

import org.junit.Test;

public class HttpChannelTest {
	@Test
	public void testSerialization() {
		Channel source = new HttpChannel("test");
		Channel target = new HttpChannel("test");
		assertCanClone(source, target);
	}

}
