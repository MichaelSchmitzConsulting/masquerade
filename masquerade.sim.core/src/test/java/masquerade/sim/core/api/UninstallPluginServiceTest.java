package masquerade.sim.core.api;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import masquerade.sim.plugin.Plugin;
import masquerade.sim.plugin.PluginManager;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link UninstallPluginService}
 */
public class UninstallPluginServiceTest {

	private static final String PLUGIN_NAME = "some.name";
	private static final String PLUGIN_VERSION = "1.0.0";
	private UninstallPluginService service;
	private PluginManager pluginManager;

	/**
	 * Test method for {@link masquerade.sim.core.api.UninstallPluginService#uninstallPlugin(java.lang.String, javax.servlet.http.HttpServletResponse)}.
	 */
	@Test
	public void testUninstallPlugin() throws Exception {
		Plugin plugin = createStrictMock(Plugin.class);
		plugin.remove();
		replay(plugin);
		
		expect(pluginManager.getPlugin(PLUGIN_NAME, PLUGIN_VERSION)).andReturn(plugin);
		replay(pluginManager);
		
		service.uninstallPlugin(PLUGIN_NAME + ":" + PLUGIN_VERSION, null);
		
		verify(pluginManager, plugin);
	}

	@Before
	public void setUp() {
		service = new UninstallPluginService();
		pluginManager = createStrictMock(PluginManager.class);
		service.pluginManager = pluginManager;
	}	
}
