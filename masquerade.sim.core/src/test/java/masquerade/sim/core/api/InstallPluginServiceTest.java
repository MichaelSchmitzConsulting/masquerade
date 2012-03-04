package masquerade.sim.core.api;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.InputStream;

import masquerade.sim.plugin.Plugin;
import masquerade.sim.plugin.PluginManager;

import org.apache.commons.io.input.NullInputStream;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link InstallPluginService}
 */
public class InstallPluginServiceTest {

	private static final String PLUGIN_NAME = "some.name";
	private InstallPluginService service;
	private PluginManager pluginManager;

	/**
	 * Test method for {@link masquerade.sim.core.api.InstallPluginService#installPlugin(java.lang.String, javax.servlet.ServletInputStream)}.
	 */
	@Test
	public void testInstallPlugin() throws Exception {
		Plugin plugin = createStrictMock(Plugin.class);
		replay(plugin);
		
		InputStream inputStream = new NullInputStream(0);
		expect(pluginManager.installPlugin(PLUGIN_NAME, inputStream)).andReturn(plugin);
		replay(pluginManager);
		
		service.installPlugin(PLUGIN_NAME, inputStream);
		
		verify(pluginManager);
	}

	@Before
	public void setUp() {
		service = new InstallPluginService();
		pluginManager = createStrictMock(PluginManager.class);
		service.pluginManager = pluginManager;
	}
}
