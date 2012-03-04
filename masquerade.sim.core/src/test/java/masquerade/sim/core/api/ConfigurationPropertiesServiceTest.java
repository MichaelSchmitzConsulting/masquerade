package masquerade.sim.core.api;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import masquerade.sim.model.Settings;
import masquerade.sim.model.repository.ModelRepository;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ConfigurationPropertiesService}
 */
public class ConfigurationPropertiesServiceTest {

	private static final String PROPS = "foo=bar";
	private ModelRepository modelRepository;
	private ConfigurationPropertiesService service;

	/**
	 * Test method for {@link masquerade.sim.core.api.ConfigurationPropertiesService#setConfigurationProperties(java.lang.String)}.
	 */
	@Test
	public void testSetConfigurationProperties() {
		Settings settings = new Settings();
		
		expect(modelRepository.getSettings()).andReturn(settings);
		modelRepository.updateSettings(settings);
		replay(modelRepository);
		
		service.setConfigurationProperties(PROPS);
		
		verify(modelRepository);
		
		assertEquals(PROPS, settings.getConfigurationProperties());
	}

	@Before
	public void setUp() {
		modelRepository = createStrictMock(ModelRepository.class);
		
		service = new ConfigurationPropertiesService();
		service.modelRepository = modelRepository;		
	}
}
