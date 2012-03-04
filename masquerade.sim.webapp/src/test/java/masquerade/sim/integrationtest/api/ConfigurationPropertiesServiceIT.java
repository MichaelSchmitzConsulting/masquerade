package masquerade.sim.integrationtest.api;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * Test case for the configuration properties API service
 */
public class ConfigurationPropertiesServiceIT extends ClientBased {
	@Test
	public void testSetAndGetConfigurationProperties() {
		Map<String, String> props = createProps();
		testIfReturnedPropsMatch(props);
	}
	
	@Test
	public void testSetAndGetConfigurationPropertiesBig() {
		Map<String, String> props = createMoreProps();
		testIfReturnedPropsMatch(props);
	}

	@Test
	public void testSetAndGetConfigurationPropertiesEmpty() {
		Map<String, String> props = createEmptyProps();
		testIfReturnedPropsMatch(props);
	}

	private void testIfReturnedPropsMatch(Map<String, String> props) {
		client().setConfigurationProperties(props);
		
		Map<String, String> returnedProps = client().getConfigurationProperties();
		
		assertEquals(props, returnedProps);
	}

	private Map<String, String> createEmptyProps() {
		return Collections.<String, String>emptyMap();
	}

	private static Map<String, String> createProps() {
		Map<String, String> props = new HashMap<String, String>();
		props.put("a", "b");
		props.put("c", "d");
		return props;
	}

	private static Map<String, String> createMoreProps() {
		Map<String, String> props = new HashMap<String, String>();
		for (char c = '0'; c < 'z'; c++) {
			props.put(String.valueOf(c), String.valueOf(c) + String.valueOf(c));
		}		
		return props;
	}
}
