package masquerade.sim.channel.http;

import static masquerade.sim.util.BeanCloneAssert.assertCanClone;
import masquerade.sim.model.Channel;

import org.junit.Test;

public class HttpStandaloneChannelTest {
	@Test
	public void testSerialization() {
		Channel source = new HttpStandaloneChannel("test");
		Channel target = new HttpStandaloneChannel("test");
		assertCanClone(source, target);
	}
}
